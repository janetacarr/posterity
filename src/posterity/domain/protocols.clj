(ns posterity.domain.protocols
  "Central namespace for protocols. Don't require things in this namespace."
  (:gen-class))

(defprotocol addon-config
  (config-map [this] "Returns a config map."))

(defprotocol event-bus
  (put-event! [this event] "Puts a non-nil event on the event bus. true if successful, and nil if there was an error.")
  (pull-event! [this] "Returns the event bus as a core.async channel"))
