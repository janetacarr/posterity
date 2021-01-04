(ns posterity.core.jira.configurator
  (:require [posterity.db.core :refer [new-db-spec]]
            [posterity.db.installs :refer [->Installs]]
            [posterity.domain.protocols :as p]))

(defrecord JiraConfigurator [installer]
  p/Configurator
  (install-complete? [this client-key product-type]
    (some? (:customer-id (p/get-install! installer client-key product-type)))))

(defn jira-configurator
  []
  (let [installer (->Installs (new-db-spec {}))]
    (->JiraConfigurator installer)))
