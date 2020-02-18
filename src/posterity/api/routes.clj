(ns posterity.api.routes
  (:require [posterity.api.handlers :as handlers]
            [posterity.api.views :as views]
            [posterity.eventq.core :refer [eventq]]))

(def api-routes
  [["/events" {:post {}
               :handler (handlers/webhook-event!)}]])

(def rest-routes
  (do
    (into api-routes views/view-routes)))
