(ns posterity.middleware
  (:require [cheshire.core :as json]
            [byte-streams :as bs]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [reitit.ring.middleware.exception :as exception]
            [manifold.deferred :as d]
            [manifold.stream :as s]))

(defn bytestream->map
  [bytestream]
  (-> bytestream
      bs/to-string
      (json/parse-string csk/->kebab-case-keyword)))

(defn map->bytestream
  [m]
  (-> m
      (json/generate-string csk/->camelCase)
      (bs/to-byte-buffer)))

(defn wrap-params [handler]
  (fn [request]
    (let [headers (->> request :headers (cske/transform-keys csk/->kebab-case-keyword))
          body (-> request :body bytestream->map)]
      (handler (assoc request :body body :headers headers)))))

(derive ::error ::exception)
(derive ::failure ::exception)
(derive ::horror ::exception)

(defn handler [message exception request]
  (let [body (map->bytestream {:message message
                               :exception (.getMessage exception)
                               :data (ex-data exception)
                               :uri (:uri request)})]
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
                        (println "ERROR" (pr-str (:uri request)))
                        (handler e request))})))
