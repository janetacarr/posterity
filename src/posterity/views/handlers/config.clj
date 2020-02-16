(ns posterity.views.handlers.config
  (:require [posterity.domain.protocols :as p]
            [posterity.config.core :as config]
            [clj-json.core :as json]))

(defn get-atlassian-connect
  [req]
  {:status 200
   :headers {"content-type" "application/json"}
   :body (json/generate-string (p/config-map config/atlassian-descriptor))})
