(ns posterity.eventq.core
  (:require [posterity.domain.protocols :as p]
            [clojure.core.async :as a]
            [posterity.eventq.puller :as puller]
            [mount.core :refer [defstate]]
            [taoensso.timbre :as log]))

(defrecord EventQ [ch]
  p/event-bus
  (put-event! [this event]
    (a/put! ch event (fn [open?]
                       (when-not open?
                         (log/error "event bus is closed")))))
  (pull-event! [this]
    ch))

(defn start
  []
  (let [event-bus (->EventQ (a/chan))]
    (log/info "event bus started")
    (puller/start-polling! event-bus)
    event-bus))

(defn stop [eventq]
  (a/close! (p/pull-event! eventq)))

(defstate eventq
  :start
  (start)
  :stop
  (stop eventq))
