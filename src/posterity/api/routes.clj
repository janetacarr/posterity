(ns posterity.api.routes
  (:require [posterity.api.handlers :as handlers]
            [posterity.api.views :as views]
            [posterity.eventq.core :refer [eventq]]
            [posterity.middleware :refer [wrap-params]]))

(def api-routes
  [["/events" {:post {:middleware [wrap-params]}
               :handler (handlers/webhook-event!)}]
   ["/jira" {:middleware [wrap-params]}
    ["/install" {:post {}
                 :handler (handlers/app-installed nil)}]
    ["/uninstall" {:post {}
                   :handler (handlers/app-uninstalled nil)}]
    ["/enabled" {:post {}
                 :handler (handlers/app-enabled nil)}]
    ["/disabled" {:post {}
                  :handler (handlers/app-disabled nil)}]]
   ["/confluence" {:middleware [wrap-params]}
    ["/install" {:post {}
                 :handler (handlers/app-installed nil)}]
    ["/uninstall" {:post {}
                   :handler (handlers/app-uninstalled nil)}]
    ["/enabled" {:post {}
                 :handler (handlers/app-enabled nil)}]
    ["/disabled" {:post {}
                  :handler (handlers/app-disabled nil)}]]])

(def rest-routes
  (do
    (into api-routes views/view-routes)))
