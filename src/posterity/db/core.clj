(ns posterity.db.core
  (:require [mount.core :refer [defstate]]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [posterity.config :refer [env]]))

(defn new-db-spec
  [opts]
  (-> {}
      (assoc :connection-uri (:database-url opts))))

(defstate db
  :start
  (new-db-spec env)
  :stop
  (try
    (let [db-con (jdbc/db-connection db)]
      (.close db-con))
    (catch Exception e (log/info (str (.getMessage e) (" while shutting down"))))))
