(ns posterity.api.views
  (:require [posterity.views.handlers.config :as config-handlers]
            [posterity.views.handlers.pages :as pages-handlers]
            [posterity.middleware :refer [wrap-params
                                          wrap-authentication]]))

(def view-routes
  [["/jira"
    ["/settings.html" {:get {}
                       :middleware [wrap-params
                                    (partial wrap-authentication "jira")]
                       :handler pages-handlers/jira-configuration-page}]
    ["/atlassian-connect.json" {:get {}
                                :handler config-handlers/get-jira-connect}]]

   ["/confluence"
    ["/configure.html" {:get {}
                        :middleware [wrap-params
                                     (partial wrap-authentication "confluence")]
                        :handler pages-handlers/confluence-config-page}]
    ["/atlassian-connect.json" {:get {}
                                :handler config-handlers/get-confluence-connect}]]])
