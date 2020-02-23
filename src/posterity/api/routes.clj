(ns posterity.api.routes
  (:require [posterity.api.handlers :as handlers]
            [posterity.api.views :as views]
            [posterity.eventq.core :refer [eventq]]
            [posterity.middleware :refer [wrap-params wrap-lifecycle-request]]
            [posterity.settings.jira.lifecycle :as jl]
            [posterity.settings.confluence.lifecycle :as cl]))

(def api-routes
  [["/events" {:post {:middleware [wrap-params]}
               :handler (handlers/webhook-event!)}]

   (let [jira-lc (jl/->JiraLifecycle)]
     ["/jira" {:middleware [wrap-params
                            wrap-lifecycle-request]}
      ["/install" {:post {}
                   :handler (handlers/app-installed jira-lc)}]
      ["/uninstall" {:post {}
                     :handler (handlers/app-uninstalled jira-lc)}]
      ["/enabled" {:post {}
                   :handler (handlers/app-enabled jira-lc)}]
      ["/disabled" {:post {}
                    :handler (handlers/app-disabled jira-lc)}]])
   (let [confluence-lc (cl/->ConfluenceLifecycle)]
     ["/confluence" {:middleware [wrap-params
                                  wrap-lifecycle-request]}
      ["/install" {:post {}
                   :handler (handlers/app-installed confluence-lc)}]
      ["/uninstall" {:post {}
                     :handler (handlers/app-uninstalled confluence-lc)}]
      ["/enabled" {:post {}
                   :handler (handlers/app-enabled confluence-lc)}]
      ["/disabled" {:post {}
                    :handler (handlers/app-disabled confluence-lc)}]])])

(def rest-routes
  (do
    (into api-routes views/view-routes)))
