(ns posterity.db.customers
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [honeysql.core :as hsql]
            [honeysql.helpers :as hsqlh]
            [posterity.domain.protocols :as p]))

(defrecord Customers [db]
  p/CustomerEntity
  (create-customer! [this]
    (try
      (jdbc/with-db-transaction [db-con db]
        (let [customer-is-created
              (->> (hsql/build :insert-into :customers :values [{:ts :current_timestamp}])
                   (hsql/format)
                   (jdbc/execute! db-con)
                   (empty?)
                   (not))]
          (when customer-is-created
            (->> (hsql/build :select :* :from :customers
                             :order-by [[:customer_id :desc]]
                             :limit 1)
                 (hsql/format)
                 (jdbc/query db-con)
                 (first)
                 (cske/transform-keys csk/->kebab-case-keyword)))))
      (catch Exception e
        (log/error "unable to create customer entry in db" (.getMessage e)))))

  (get-customer [this id]
    (try
      (jdbc/with-db-connection [db-con db]
        (->> (hsql/build :select :* :from :customers :where [:= :customer_id id])
             (hsql/format)
             (jdbc/query db-con)
             (first)
             (cske/transform-keys csk/->kebab-case-keyword)))
      (catch Exception e
        (log/warn "unable to get customer by id" id (.getMessage e)))))

  (update-customer! [this id timestamp]
    (try
      (jdbc/with-db-connection [db-con db]
        (letfn [(exec! [q]
                  (jdbc/execute! db-con q))]
          (-> (hsqlh/update :customers)
              (hsqlh/sset {:ts timestamp})
              (hsqlh/where [:= :customer_id id])
              (hsql/format)
              (exec!)
              (empty?)
              (not))))
      (catch Exception e
        (log/warn "unable to update customer" id (.getMessage e)))))

  (delete-customer! [this id]
    (try
      (jdbc/with-db-connection [db-con db]
        (->> (hsql/build :delete :from :customers :where [:= :customer_id id])
             (hsql/format)
             (empty?)
             (not)))
      (catch Exception e
        (log/warn "unable to delete customer" id (.getMessage e)))))

  (list-customers [this]
    (try
      (jdbc/with-db-connection [db-con db]
        (->> (hsql/build :select :* :from :customers)
             (hsql/format)
             (jdbc/query db-con)
             (mapv (fn [cust]
                     (cske/transform-keys csk/->kebab-case-keyword cust)))
             (seq)))
      (catch Exception e
        (log/warn "unable to retrieve all customers from the db" (.getMessage e))))))
