(ns posterity.api.handlers
  (:require [clojure.tools.logging :as log]
            [clj-json.core :as json]
            [byte-streams :as bs]
            [posterity.eventq.core :refer [eventq]]
            [posterity.domain.protocols :as p]))

(defn bytestream->map
  [bytestream]
  (-> bytestream
      bs/to-string
      json/parse-string))

(defn webhook-event!
  []
  (fn [req]
    (let [request-body (bytestream->map (:body req))]
      (when-not (nil? request-body)
        (p/put-event! eventq request-body))
      (log/info "headers: ==============> "(:headers req))
      (log/info "Received webhook event: " request-body))
    {:status 200}))

(defn app-installed
  []
  (fn [req]
    (let [request-body (bytestream->map (:body req))]
      (log/info "Received install callback: " request-body)
      {:status 200})))
