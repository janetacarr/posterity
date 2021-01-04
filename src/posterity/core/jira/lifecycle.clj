(ns posterity.core.jira.lifecycle
  (:require [posterity.domain.protocols :as p]
            [posterity.db.core :refer [db new-db-spec]]
            [posterity.db.customers :refer [->Customers]]
            [posterity.db.installs :refer [->Installs]]))



(defrecord JiraLifecycle [customer installer]
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
                                                       (str base-url "/wiki")
                                                       "confluence")
          _ (when (and (:install-id companion)
                       (nil? (:customer-id companion)))
              (p/connect-product-instances! installer
                                            (:install-id installation)
                                            (:install-id companion)))]
      installation))

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
    "")

  (disabled! [this {:keys [key client-key account-id shared-secret base-url display-url
                           display-url-servicedesk-help-center product-type description
                           service-entitlement-number oauth-client-id] :as disabled-payload}]
    "Takes an disabled payload map, and returns true if the customer was disabled correctly. Nil if not"))

(defn jira-lifecycle
  []
  (let [customer (->Customers (new-db-spec {}))
        installer (->Installs (new-db-spec {}))]
    (->JiraLifecycle customer installer)))
