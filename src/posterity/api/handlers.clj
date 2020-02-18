(ns posterity.api.handlers
  (:require [clojure.tools.logging :as log]
            [clj-json.core :as json]
            [byte-streams :as bs]
            [posterity.eventq.core :refer [eventq]]
            [posterity.domain.protocols :as p]))

(defn webhook-event!
  []
  (fn [req]
    (let [request-body (-> req
                           :body
                           bs/to-string
                           json/parse-string)]
      (when-not (nil? request-body)
        (p/put-event! eventq request-body))
      (log/info "Received webhook event: " request-body))
    {:status 200}))
