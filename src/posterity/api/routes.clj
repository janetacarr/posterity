(ns posterity.api.routes
  (:require [posterity.api.handlers :as handlers]
            [posterity.api.views :as views]
            [posterity.core.jira.lifecycle :as jl]
            [posterity.core.confluence.lifecycle :as cl]
            [posterity.eventq.core :refer [eventq]]
            [posterity.middleware :refer [wrap-params
                                          wrap-lifecycle-request
                                          wrap-authentication]]))

(def api-routes
  [["/jira"
    ["/events" {:post {}
                :middleware [wrap-params
                             (partial wrap-authentication "jira")]
                :handler (handlers/webhook-event!)}]]

   (let [jira-lc (jl/jira-lifecycle)]
     ["/jira" {:middleware [wrap-params
                            wrap-lifecycle-request]}

      ["/install" {:post {}
                   :handler (handlers/app-installed jira-lc)}]
      ["/uninstall" {:post {}
                     :middleware [(partial wrap-authentication "jira")]
                     :handler (handlers/app-uninstalled jira-lc)}]
      ["/enabled" {:post {}
                   :middleware [(partial wrap-authentication "jira")]
                   :handler (handlers/app-enabled jira-lc)}]
      ["/disabled" {:post {}
                    :middleware [(partial wrap-authentication "jira")]
                    :handler (handlers/app-disabled jira-lc)}]])
   (let [confluence-lc (cl/confluence-lifecycle)]
     ["/confluence" {:middleware [wrap-params
                                  wrap-lifecycle-request]}
      ["/install" {:post {}
                   :handler (handlers/app-installed confluence-lc)}]
      ["/uninstall" {:post {}
                     :middleware [(partial wrap-authentication "confluence")]
                     :handler (handlers/app-uninstalled confluence-lc)}]
      ["/enabled" {:post {}
                   :middleware [(partial wrap-authentication "confluence")]
                   :handler (handlers/app-enabled confluence-lc)}]
      ["/disabled" {:post {}
                    :middleware [(partial wrap-authentication "confluence")]
                    :handler (handlers/app-disabled confluence-lc)}]])])

(def rest-routes
  (do
    (into api-routes views/view-routes)))
