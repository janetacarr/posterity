(ns posterity.core.confluence.lifecycle
  (:require [posterity.domain.protocols :as p]
            [posterity.db.core :refer [db new-db-spec]]
            [posterity.db.customers :refer [->Customers]]
            [posterity.db.installs :refer [->Installs]]))

(defrecord ConfluenceLifecycle [customer installer]
  p/CustomerLifecycle
  (installed! [this
               {:keys [key client-key account-id shared-secret base-url display-url
                       display-url-servicedesk-help-center product-type description
                       service-entitlement-number oauth-client-id]
                :as install-payload}]
    (let [installation (p/get-install! installer client-key product-type)
          installation (when (nil? installation)
                         (p/create-install! installer nil key client-key
                                            account-id shared-secret
                                            base-url display-url
                                            display-url-servicedesk-help-center
                                            product-type description
                                            ""
                                            oauth-client-id))
          companion (p/get-install-by-base-and-product installer
                                                       ()
                                                       "jira")]
      ))

  (uninstalled! [this {:keys [key client-key account-id shared-secret base-url display-url
                              display-url-servicedesk-help-center product-type description
                              service-entitlement-number oauth-client-id] :as uninstall-payload}]
    (when-let [installation (p/get-install! installer client-key product-type)]
      (p/delete-install! installer (:install-id installation))))

  (enabled! [this
             {:keys [key client-key account-id shared-secret base-url display-url
                     display-url-servicedesk-help-center product-type description
                     service-entitlement-number oauth-client-id]
              :as enabled-payload}]
    (when-let [enabled (p/enable! installer client-key product-type)]
      enabled))

  (disabled! [this {:keys [key client-key account-id shared-secret base-url display-url
                           display-url-servicedesk-help-center product-type description
                           service-entitlement-number oauth-client-id] :as disabled-payload}]
    (when-let [disabled (p/disable! installer client-key product-type)]
      disabled)))

(defn confluence-lifecycle
  []
  (let [customer (->Customers (new-db-spec {}))
        installer (->Installs (new-db-spec {}))]
    (->ConfluenceLifecycle customer installer)))
