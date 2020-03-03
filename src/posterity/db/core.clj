(ns posterity.db.core
  (:require [mount.core :refer [defstate]]
            [clojure.java.jdbc :as jdbc]
            [posterity.config :refer [env]]
            [taoensso.timbre :as log]))

(defn new-db-spec
  [opts]
  (-> {:dbtype "postgres"
       :dbname "posterity"
       :user "postgres"
       :password "postgres"}
      (assoc :connection-uri (:database-url opts))))

;; FIXME: database mount is not dereferencing when passed to clojure.java.jdbc
(defstate db
  :start
  (new-db-spec env)
  :stop
  (try
    (let [db-con (jdbc/db-connection db)]
      (.close db-con))
    (catch Exception e (log/info (str (.getMessage e) (" while shutting down"))))))
