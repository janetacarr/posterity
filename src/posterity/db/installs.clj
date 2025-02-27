(ns posterity.db.installs
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [honeysql.core :as hsql]
            [honeysql.helpers :as hsqlh]
            [posterity.domain.protocols :as p]
            [taoensso.timbre :as log]))

(defrecord Installs [db]
  p/InstallEntity
  (create-install! [this customer-id key client-key account-id shared-secret
                    base-url display-url service-url product-type description
                    service-entitlement-number oauth-client-id]
    (try
      (jdbc/with-db-transaction [db-con db]
        (let [pt (case product-type
                   "jira" (hsql/raw "'jira'")
                   "confluence" (hsql/raw "'confluence'")
                   (log/warn "product-type must be either 'jira' or 'confluence'"))]
          (when-not (nil? pt)
            (let [install-is-created
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
                       (jdbc/execute! db-con)
                       (empty?)
                       (not))]
              (when install-is-created
                (->> (hsql/build :select :* :from :installs
                                 :order-by [[:install_id :desc]]
                                 :limit 1)
                     (hsql/format)
                     (jdbc/query db-con)
                     (first)
                     (cske/transform-keys csk/->kebab-case-keyword)))))))
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

  (update-install! [this id key account-id base-url display-url service-url
                    description oauth-client-id enabled?]
    (try
      (jdbc/with-db-connection [db-con db]
        (letfn [(exec! [q]
                  (jdbc/execute! db-con q))]
          (let [install-is-updated
                (-> (hsqlh/update :installs)
                    (hsqlh/sset {:key key
                                 :account_id account-id
                                 :base-url base-url
                                 :display_url display-url
                                 :display_url_service_help_center service-url
                                 :description description
                                 :oauth_client_id oauth-client-id
                                 :enabled enabled?})
                    (hsqlh/where [:= :install_id id])
                    (hsql/format)
                    (exec!)
                    (empty?)
                    (not))]
            (when install-is-updated
              (->> (hsql/build :select :* :from :installs
                               :where [:= :install_id id])
                   (hsql/format)
                   (jdbc/query db-con)
                   (first)
                   (cske/transform-keys csk/->kebab-case-keyword))))))
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
        (log/warn "unable to delete installation" id (.getMessage e))))))

(extend-type Installs
  p/EnableEntity
  (enable! [this client-key product-type]
    (try
      (let [db (:db this)
            pt (case product-type
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

  (disable! [this client-key product-type]
    (try
      (let [db (:db this)
            pt (case product-type
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

(extend-type Installs
  p/CrossInstall
  (get-install-by-base-and-product [this base-url product-type]
    (try
      (let [db (:db this)
            pt (case product-type
                 "jira" (hsql/raw "'jira'")
                 "confluence" (hsql/raw "'confluence'")
                 (log/warn "product-type must be either 'jira' or 'confluence'"))]
        (when-not (nil? pt)
          (->> (hsql/build :select :* :from :installs
                           :where [:and
                                   [:= :base_url base-url]
                                   [:= :product_type pt]])
               (hsql/format)
               (jdbc/query db)
               (first)
               (cske/transform-keys csk/->kebab-case-keyword))))
      (catch Exception e
        (log/error
         "unable to get install info by base-url and product-type"
         base-url
         product-type
         (.getMessage e)))))

  (connect-product-instances! [this source-id target-id]
    (try
      (let [db (:db this)
            customer
            (jdbc/with-db-transaction [db-con db]
              (let [customer-is-created
                    (->> (hsql/build :insert-into :customers :values [{:ts :current_timestamp}])
                         (hsql/format)
                         (jdbc/execute! db-con)
                         (empty?)
                         (not))
                    customer (if customer-is-created
                               (->> (hsql/build :select :* :from :customers
                                                :order-by [[:customer_id :desc]]
                                                :limit 1)
                                    (hsql/format)
                                    (jdbc/query db-con)
                                    (first)
                                    (cske/transform-keys csk/->kebab-case-keyword))
                               (throw (ex-info "Could not create customer" customer-is-created)))]
                customer))]
        (jdbc/with-db-connection [db (:db this)]
          (letfn [(exec! [q]
                    (jdbc/execute! db q))]
            (let [source-is-updated
                  (-> (hsqlh/update :installs)
                      (hsqlh/sset {:customer_id (:customer-id customer)})
                      (hsqlh/where [:= :install_id source-id])
                      (hsql/format)
                      (exec!)
                      (empty?)
                      (not)
                      (or (throw
                           (ex-info
                            "could not update source with customer"
                            customer))))
                  target-is-updated
                  (-> (hsqlh/update :installs)
                      (hsqlh/sset {:customer_id (:customer-id customer)})
                      (hsqlh/where [:= :install_id target-id])
                      (hsql/format)
                      (exec!)
                      (empty?)
                      (not)
                      (or (throw
                           (ex-info
                            "could not update target"
                            customer))))]
              (and source-is-updated target-is-updated)))))
      (catch Exception e
        (log/error
         "unable to connect product instances"
         source-id
         target-id
         (.getMessage e))))))
