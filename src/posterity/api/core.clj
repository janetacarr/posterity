(ns posterity.api.core
  (:require [aleph.http :as http]
            [reitit.ring :as ring]
            [reitit.coercion.spec :as s]
            [reitit.ring.coercion :as rrc]
            [posterity.middleware :as ex]
            [posterity.api.routes :as routes])
  (:gen-class))

(def app
  (ring/ring-handler
   (ring/router
    routes/rest-routes
    {:data {:coercion reitit.coercion.spec/coercion
            :middleware [rrc/coerce-exceptions-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware
                         ex/exception-middleware]}})
   (ring/create-default-handler)))
