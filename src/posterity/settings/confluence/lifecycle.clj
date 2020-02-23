(ns posterity.settings.confluence.lifecycle
  (:require [posterity.domain.protocols :as p]))



(defrecord ConfluenceLifecycle []
  p/CustomerLifecycle
  (installed! [this
               {:keys [key client-key account-id shared-secret base-url display-url
                       display-url-servicedesk-help-center product-type description
                       service-entitlement-number oauth-client-id]
                :as install-payload}]
    )

  (uninstalled! [this {:keys [key client-key account-id shared-secret base-url display-url
                              display-url-servicedesk-help-center product-type description
                              service-entitlement-number oauth-client-id] :as uninstall-payload}]
    "Takes an uninstall payload map, and returns true if the customer uninstall was handled correctly. Nil if not")

  (enabled! [this
             {:keys [key client-key account-id shared-secret base-url display-url
                     display-url-servicedesk-help-center product-type description
                     service-entitlement-number oauth-client-id]
              :as enabled-payload}]
    "")

  (disabled! [this {:keys [key client-key account-id shared-secret base-url display-url
                           display-url-servicedesk-help-center product-type description
                           service-entitlement-number oauth-client-id] :as disabled-payload}]
    "Takes an disabled payload map, and returns true if the customer was disabled correctly. Nil if not"))
