(ns posterity.api.routes
  (:require [posterity.api.views :as views]))

(def api-routes
  [["/events" {:post
               }]])

(def rest-routes
  (into api-routes views/view-routes))
