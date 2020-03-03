(ns posterity.middleware
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as budmid]
            [cheshire.core :as json]
            [clojure.spec.alpha :as s]
            [byte-streams :as bs]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [posterity.domain.protocols :as p]
            [posterity.core.token :refer [auth-token-installs]]
            [reitit.ring.middleware.exception :as exception]
            [taoensso.timbre :as log]))

(defn bytestream->map
  [bytestream]
  (some-> bytestream
          bs/to-string
          (json/parse-string csk/->kebab-case-keyword)))

(defn map->bytestream
  [m]
  (-> m
      (json/generate-string csk/->camelCase)
      (bs/to-byte-buffer)))

(defn querystr->map
  [s]
  (letfn [(vs->m
            [vs]
            (reduce (fn [acc x]
                      (let [[key val] (clojure.string/split x #"=")
                            key (csk/->kebab-case-keyword key)]
                        (assoc acc (keyword key) val)))
                    {} vs))]
    (some-> s
            (clojure.string/split #"&")
            (vs->m))))

(defn wrap-params [handler]
  (fn [request]
    (let [headers (->> request :headers)
          body (-> request :body bytestream->map)
          query-params (some-> request :query-string querystr->map)]
      (handler (assoc request :body body :headers headers :query-params query-params)))))

(derive ::error ::exception)
(derive ::failure ::exception)
(derive ::horror ::exception)

(defn handler [message exception request]
  (let [body (bs/to-byte-buffer "Internal Server Error")]
    (log/debug "Internal server error: " {:message message
                                          :exception (.getMessage exception)
                                          :data (ex-data exception)
                                          :uri (:uri request)})
    {:status 500
     :body body}))

(def exception-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers
    {;; ex-data with :type ::error
     ::error (partial handler "error")

     ;; ex-data with ::exception or ::failure
     ::exception (partial handler "exception")

     ;; SQLException and all it's child classes
     java.sql.SQLException (partial handler "sql-exception")

     ;; override the default handler
     ::exception/default (partial handler "default")

     ;; print stack-traces for all exceptions
     ::exception/wrap (fn [handler e request]
                        (log/error "ERROR" (pr-str (:uri request)))
                        (handler e request))})))

(s/def ::key string?)
(s/def ::client-key string?)
(s/def ::shared-secret string?)
(s/def ::server-version string?)
(s/def ::plugins-version string?)
(s/def ::base-url string?)
(s/def ::display-url string?)
(s/def ::display-url-servicedesk-help-center string?)
(s/def ::product-type #{"jira" "confluence"})
(s/def ::description string?)
(s/def ::service-entitlement-number string?)
(s/def ::oauth-client-id string?)
(s/def ::public-key string?)
(s/def ::account-id string?)
(s/def ::event-type #{"installed" "uninstalled" "enabled" "disabled"})

(s/def ::body (s/keys :req-un
                      [::key
                       ::client-key
                       ::base-url
                       ::product-type
                       ::event-type]
                      :opt-un
                      [::shared-secret
                       ::server-version
                       ::plugins-version
                       ::display-url
                       ::display-url-servicedesk-help-center
                       ::description
                       ::service-entitlement-number
                       ::account-id
                       ::public-key
                       ::oauth-client-id]))

(defn wrap-lifecycle-request
  [handler]
  (fn [request]
    (let [{:keys [body headers]} request
          is-valid (s/valid? ::body body)]
      (if is-valid
        (handler request)
        (do
          (log/error "received invalid lifecycle event" body)
          {:status 400})))))


;; TODO: Added query string hash (qsh) validation
(defn wrap-authentication
  [product-type handler]
  (fn [request]
    (let [token (or (some->> request
                             :query-params
                             :jwt
                             (str "JWT "))
                    (some->> request
                             :query-params
                             :JWT
                             (str "JWT "))
                    (get-in request [:headers "authorization"]))
          request (assoc-in request [:headers "authorization"] token)
          secret (-> product-type
                     (auth-token-installs)
                     (p/get-secret-bytes token))
          backend (backends/jws {:secret secret
                                 :token-name "JWT"})]
      (if-not (nil? (budmid/authenticate-request request [backend]))
        (handler request)
        {:status 401}))))
