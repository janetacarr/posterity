(ns posterity.core
  (:require [posterity.api.core :as api]
            [posterity.http :as http]
            [posterity.config :refer [env]]
            [posterity.eventq.core :refer [eventq]]
            [mount.core :as mount]
            [clojure.tools.logging :as log]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])


(mount/defstate ^{:on-reload :noop} http-server
  :start
  (http/start
   (-> env
       (assoc  :handler api/app)
       (update :io-threads #(or % (* 2 (.availableProcessors (Runtime/getRuntime)))))
       (update :port #(or (-> env :options :port) %))))
  :stop
  (http/stop http-server))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (mount/start #'posterity.config/env)
  (cond
    (nil? (:database-url env))
    (do (log/error "DATABASE_URL not set.")
        (System/exit 1)))
  :else
  (start-app args))
