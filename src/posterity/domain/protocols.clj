(ns posterity.domain.protocols
  "Central namespace for protocols. Don't require things in this namespace."
  (:gen-class))

(defprotocol addon-config
  (config-map [this] "Returns a config map."))

(defprotocol event-bus
  (put-event! [this event] "Puts a non-nil event on the event bus. true if successful, and nil if there was an error.")
  (pull-event! [this] "Returns the event bus as a core.async channel"))

(defprotocol customer-lifecycle
  (installed! [this {:keys [key client-key account-id shared-secret base-url display-url
                            display-url-servicedesk-help-center product-type description
                            service-entitlement-number oauth-client-id] :as install-payload}]
    "Takes a install payload map, and returns true if the customer install was handled correctly. Nil if not")

  (uninstalled! [this {:keys [key client-key account-id shared-secret base-url display-url
                              display-url-servicedesk-help-center product-type description
                              service-entitlement-number oauth-client-id] :as uninstall-payload}]
    "Takes an uninstall payload map, and returns true if the customer uninstall was handled correctly. Nil if not")

  (enabled! [this {:keys [key client-key account-id shared-secret base-url display-url
                          display-url-servicedesk-help-center product-type description
                          service-entitlement-number oauth-client-id] :as enabled-payload}]
    "Takes an enabled payload map, and returns true if the customer was enabled correctly. Nil if not")

  (disabled! [this {:keys [key client-key account-id shared-secret base-url display-url
                           display-url-servicedesk-help-center product-type description
                           service-entitlement-number oauth-client-id] :as disabled-payload}]
    "Takes an disabled payload map, and returns true if the customer was disabled correctly. Nil if not"))

#_(defprotocol customer-entity
    (create-customer [this]))
