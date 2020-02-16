(ns posterity.api.views
  (:require [posterity.views.handlers.config :as config-handlers]
            [posterity.views.handlers.pages :as pages-handlers]))

(def view-routes
  [["/helloworld.html" {:get {}
                        :handler pages-handlers/get-hello-page}]
   ["/atlassian-connect.json" {:get {}
                               :handler config-handlers/get-atlassian-connect}]
   ])
