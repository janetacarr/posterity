(ns posterity.http
  (:require [clojure.tools.logging :as log]
            [aleph.http :as http]
            [aleph.netty :as netty]
            [clojure.tools.logging :as log]
            [clojure.tools.cli :refer [parse-opts]]))

(defn start [{:keys [handler port] :as opts}]
  (try
    (log/info "starting HTTP server on port" port)
    (let
        [server (http/start-server
                 handler
                 (dissoc opts :handler))]
      (future (netty/wait-for-close server))
      server)
    (catch Throwable t
      (log/error t (str "server failed to start on port " port))
      (throw t))))

(defn stop [server]
  (.close server)
  (log/info "HTTP server stopped"))
