(ns posterity.api.routes
  (:require [posterity.api.views :as views]))

(def api-routes
  [])

(def rest-routes
  (into api-routes views/view-routes))
