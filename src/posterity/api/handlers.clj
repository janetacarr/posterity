(ns posterity.api.handlers
  (:require [clojure.spec.alpha :as s]
            [cheshire.core :as json]
            [byte-streams :as bs]
            [posterity.eventq.core :refer [eventq]]
            [posterity.domain.protocols :as p]
            [camel-snake-kebab.core :as csk]
            [taoensso.timbre :as log]))

(defn bytestream->map
  [bytestream]
  (-> bytestream
      bs/to-string
      (json/parse-string csk/->kebab-case-keyword)))

(defn webhook-event!
  []
  (fn [req]
    (let [request-body (:body req)]
      (when-not (nil? request-body)
        (p/put-event! eventq request-body))
      (log/debug "Whole request:" req)
      (log/debug "headers: ==============> "(:headers req))
      (log/debug "Received webhook event: " request-body))
    {:status 200}))

(defn app-installed
  [lifecycle]
  (fn install-handler
    [req]
    (let [{:keys [body headers]} req]
      (log/debug "Received install callback: " body)
      (if (nil? (p/installed! lifecycle body))
        {:status 400}
        {:status 204}))))

(defn app-uninstalled
  [lifecycle]
  (fn uninstall-handler
    [req]
    (let [{:keys [body headers]} req]
      (log/debug "Received uninstall callback: " req)
      (if (nil? (p/uninstalled! lifecycle body))
        {:status 400}
        {:status 200}))))

(defn app-enabled
  [lifecycle]
  (fn enable-handler
    [req]
    (let [{:keys [body headers]} req]
      (log/debug "Received enabled callback: " body)
      (if-let [enable-install (p/enabled! lifecycle body)]
        {:status 200}
        {:status 400}))))

(defn app-disabled
  [lifecycle]
  (fn disable-handler
    [req]
    (let [{:keys [body headers]} req]
      (log/debug "Received disabled callback: " req)
      (if-let [disable-install (p/disabled! lifecycle body)]
        {:status 200}
        {:status 400}))))
