(ns posterity.eventq.puller
  (:require [posterity.domain.protocols :as p]
            [clojure.tools.logging :as log]
            [clojure.core.async :refer [go-loop] :as a]
            [mount.core :refer [defstate]]))

(defn start-polling!
  [event-bus]
  (log/info "started polling for events")
  (go-loop [event-chan (p/pull-event! event-bus)]
    (let [event (a/<! event-chan)]
      (if (nil? event)
        (log/info "polling channel closed")
        (log/info "polled event from bus: " event)))
    (recur event-chan)))
