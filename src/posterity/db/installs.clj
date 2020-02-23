(ns posterity.db.installs
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [honeysql.core :as hsql]
            [honeysql.helpers :as hsqlh]
            [posterity.domain.protocols :as p]))

(defrecord Installs [db]
  p/InstallEntity
  (create-install! [this customer-id key client-key account-id shared-secret
                    base-url display-url service-url product-type description
                    service-entitlement-number oauth-client-id]
    (try
      (let [pt (case product-type
                 "jira" (hsql/raw "'jira'")
                 "confluence" (hsql/raw "'confluence'")
                 (log/warn "product-type must be either 'jira' or 'confluence'"))]
        (when-not (nil? pt)
          (->> (hsql/build :insert-into :installs
                           :values
                           [{:customer_id customer-id
                             :key key
                             :client_key client-key
                             :account_id account-id
                             :shared_secret shared-secret
                             :base_url base-url
                             :display_url display-url
                             :display_url_service_help_center service-url
                             :product_type pt
                             :description description
                             :service_entitlement_number service-entitlement-number
                             :oauth_client_id oauth-client-id}])
               (hsql/format)
               (jdbc/execute! db)
               (empty?)
               (not))))
      (catch Exception e
        (log/error "unable to create install entry in db" (.getMessage e)))))

  (get-install! [this client-key product-type]
    (try
      (let [pt (case product-type
                 "jira" (hsql/raw "'jira'")
                 "confluence" (hsql/raw "'confluence'")
                 (log/warn "product-type must be either 'jira' or 'confluence'"))]
        (when-not (nil? pt)
          (->> (hsql/build :select :* :from :installs
                           :where [:and
                                   [:= :client_key client-key]
                                   [:= :product_type pt]])
               (hsql/format)
               (jdbc/query db)
               (first)
               (cske/transform-keys csk/->kebab-case-keyword))))
      (catch Exception e
        (log/error
         "unable to get install info by client-key "
         client-key
         product-type
         (.getMessage e)))))

  (update-install! [this id key account-id display-url service-url
                    description oauth-client-id enabled?]
    (try
      (letfn [(exec! [q]
                (jdbc/execute! db q))]
        (-> (hsqlh/update :installs)
            (hsqlh/sset {:key key
                         :account_id account-id
                         :display_url display-url
                         :display_url_service_help_center service-url
                         :description description
                         :oauth_client_id oauth-client-id
                         :enabled enabled?})
            (hsqlh/where [:= :install_id id])
            (hsql/format)
            (exec!)
            (empty?)
            (not)))
      (catch Exception e
        (log/warn "unable to update install" id (.getMessage e)))))

  (delete-install! [this id]
    (try
      (letfn [(exec! [q]
                (jdbc/execute! db q))]
        (-> (hsqlh/delete-from :installs)
            (hsqlh/where [:= :install_id id])
            (hsql/format)
            (exec!)
            (empty?)
            (not)))
      (catch Exception e
        (log/warn "unable to delete installation" id (.getMessage e)))))

  (enable-install! [this client-key product-type]
    (try
      (let [pt (case product-type
                 "jira" (hsql/raw "'jira'")
                 "confluence" (hsql/raw "'confluence'")
                 (log/warn "product-type must be either 'jira' or 'confluence'"))]
        (when-not (nil? pt)
          (jdbc/with-db-connection [db-con db]
            (let [{:keys [install-id]} (->> (hsql/build :select :install_id :from :installs
                                                        :where [:and
                                                                [:= :client_key client-key]
                                                                [:= :product_type pt]])
                                            (hsql/format)
                                            (jdbc/query db-con)
                                            (first)
                                            (cske/transform-keys csk/->kebab-case-keyword))]
              (letfn [(exec! [q]
                        (jdbc/execute! db-con q))]
                (-> (hsqlh/update :installs)
                    (hsqlh/sset {:enabled true})
                    (hsqlh/where [:= :install_id install-id])
                    (hsql/format)
                    (exec!)
                    (empty?)
                    (not)))))))
      (catch Exception e
        (log/error "unable to enable install" client-key product-type (.getMessage e)))))

  (disable-install! [this client-key product-type]
    (try
      (let [pt (case product-type
                 "jira" (hsql/raw "'jira'")
                 "confluence" (hsql/raw "'confluence'")
                 (log/warn "product-type must be either 'jira' or 'confluence'"))]
        (when-not (nil? pt)
          (jdbc/with-db-connection [db-con db]
            (let [{:keys [install-id]} (->> (hsql/build :select :install_id :from :installs
                                                        :where [:and
                                                                [:= :client_key client-key]
                                                                [:= :product_type pt]])
                                            (hsql/format)
                                            (jdbc/query db-con)
                                            (first)
                                            (cske/transform-keys csk/->kebab-case-keyword))]
              (letfn [(exec! [q]
                        (jdbc/execute! db-con q))]
                (-> (hsqlh/update :installs)
                    (hsqlh/sset {:enabled false})
                    (hsqlh/where [:= :install_id install-id])
                    (hsql/format)
                    (exec!)
                    (empty?)
                    (not)))))))
      (catch Exception e
        (log/error "unable to enable install" client-key product-type (.getMessage e))))))
