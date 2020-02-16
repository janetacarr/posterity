(ns posterity.domain.protocols
  "Central namespace for protocols. Don't require things in this namespace."
  (:gen-class))

(defprotocol addon-config
  (config-map [this] "Returns a config map."))
