(ns posterity.db.install-keys
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [honeysql.core :as hsql]
            [honeysql.helpers :as hsqlh]
            [posterity.domain.protocols :as p]))

(defrecord InstallKeys [db]
  p/InstallEntity
  (create-install! [this install-id key client-key account-id shared-secret
                    base-url display-url service-url product-type description
                    service-entitlement-number oauth-client-id]
    )
  (get-install! [this client-key product-type])
  (update-install! [this id key account-id base-url display-url service-url
                    description oauth-client-id enabled?])
  (delete-install! [this id]))
