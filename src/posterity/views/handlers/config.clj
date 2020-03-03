(ns posterity.views.handlers.config
  (:require [posterity.domain.protocols :as p]
            [posterity.config.core :as config]
            [cheshire.core :as json]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]))

(defn get-jira-connect
  [req]
  {:status 200
   :headers {"content-type" "application/json"}
   :body (->> (config/jira-descriptor)
              (p/config-map)
              (cske/transform-keys csk/->camelCase)
              (json/generate-string))})

(defn get-confluence-connect
  [req]
  {:status 200
   :headers {"content-type" "application/json"}
   :body (->> (config/confluence-descriptor)
              (p/config-map)
              (cske/transform-keys csk/->camelCase)
              (json/generate-string))})
