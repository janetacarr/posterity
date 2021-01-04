(ns posterity.core.jira.configurator-test
  (:require [posterity.core.jira.configurator :refer [->JiraConfigurator]]
            [posterity.domain.protocols :as p]
            [clojure.test :refer [deftest is testing run-tests]]))

(defrecord TotallyRealInstaller [flip]
  p/InstallEntity
  (p/get-install! [this client-key product-type]
    (when (and (= client-key "blah") (or (= product-type "jira") (= product-type "confluence")))
      {:customer-id (when-not flip "blahblahblah")
       :install-id 0}))
  (create-install! [this customer-id key client-key account-id shared-secret
                    base-url display-url service-url product-type description
                    service-entitlement-number oauth-client-id]
    nil)
  (update-install! [this id key account-id base-url display-url service-url description
                    oauth-client-id enabled?]
    nil)
  (delete-install! [this id]
    nil))

(deftest JiraConfigurator-check-install-ends-test
  (testing "that the installation is complete for jira"
    (let [installer (->TotallyRealInstaller false)
          configurator (->JiraConfigurator installer)
          install-ends (p/install-complete? configurator "blah" "jira")]
      (is (true? install-ends))))
  (testing "that the installation is incomplete"
    (let [installer (->TotallyRealInstaller true)
          configurator (->JiraConfigurator installer)
          install-ends (p/install-complete? configurator "blah" "jira")]
      (is (false? install-ends)))))
