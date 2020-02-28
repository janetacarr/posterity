(ns posterity.domain.protocols
  "Central namespace for protocols. Don't require things in this namespace."
  (:gen-class))

(defprotocol AddonConfig
  (config-map [this] "Returns a config map."))

(defprotocol event-bus
  (put-event! [this event] "Puts a non-nil event on the event bus. true if successful, and nil if there was an error.")
  (pull-event! [this] "Returns the event bus as a core.async channel"))

(defprotocol CustomerLifecycle
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

(defprotocol CustomerEntity
  (create-customer! [this]
    "Takes nothing really, and returns the new customer or nil if there was a problem.")
  (get-customer [this id]
    "Get a customer by it's ID and return it, nil if not found.")
  (update-customer! [this id timestamp]
    "Update a customer by it's ID and returns true if successful nil otherwise. ")
  (delete-customer! [this id]
    "Deletes a customer by it's ID and returns true if successful, nil otherwise.")
  (list-customers [this]
    "Takes no arguments except this, and returns [customers"))

(defprotocol InstallEntity
  (create-install! [this customer-id key client-key account-id shared-secret
                    base-url display-url service-url product-type description
                    service-entitlement-number oauth-client-id]
    "Creates a new product installation, return the new install or nil if there was a problem")
  (get-install! [this client-key product-type]
    "Get install by client-key and product-type. Returns the install info, nil otherwise.")
  (update-install! [this id key account-id base-url display-url service-url description
                    oauth-client-id enabled?]
    "Update an install's non-critical fields. Critical fields are immutable for
     security reasons. Returns true if sucessful, nil otherwise.")
  (delete-install! [this id]
    "Delete an install. Analogous to an uninstall."))

(defprotocol EnableEntity
  (enable! [this client-key product-type])
  (disable! [this client-key product-type]))

(defprotocol TokenValidator
  (get-secret-bytes [this token]
    "Takes a JWT and product-type, extracts the issuer and gets the secret stored in the db. nil if not found."))
