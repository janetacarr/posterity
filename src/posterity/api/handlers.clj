(ns posterity.api.handlers
  (:require [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [byte-streams :as bs]
            [posterity.eventq.core :refer [eventq]]
            [posterity.domain.protocols :as p]
            [camel-snake-kebab.core :as csk]))

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
      (log/info "headers: ==============> "(:headers req))
      (log/info "Received webhook event: " request-body))
    {:status 200}))

(defn app-installed
  [lifecycle]
  (fn [req]
    (let [{:keys [body headers]} req]
      (log/info "Received install callback: " body)
      {:status 200})))

(defn app-uninstalled
  [lifecycle]
  (fn [req]
    (let [{:keys [body headers]} req]
      (log/info "Received uninstall callback: " req)
      {:status 200})))

(defn app-enabled
  [lifecycle]
  (fn [req]
    (let [{:keys [body headers]} req]
      (log/info "Received enabled callback: " body)
      {:status 200})))

(defn app-disabled
  [lifecycle]
  (fn [req]
    (let [{:keys [body headers]} req]
      (log/info "Received disabled callback: " req)
      {:status 200})))
