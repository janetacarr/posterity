(ns posterity.db.installs-test
  (:require [posterity.db.installs :as installs :refer [->Installs]]
            [posterity.domain.protocols :as p]
            [clojure.test :refer [deftest is testing run-tests]]))


(deftest create-install-test
  (testing "p/create-install! for Installs record"
    (with-redefs [clojure.java.jdbc/execute! (fn [db [query]]
                                               [1])]
      (let [db {:dbtype "postgres"
                :dbname "posterity"
                :user "postgres"
                :password "postgres"}
            jira-installed (p/create-install!
                            (->Installs db)
                            1
                            "com.addon.adhesive.digital"
                            "testkey"
                            nil
                            "testsharedsecret"
                            "blah.atlassian.net"
                            nil
                            nil
                            "jira"
                            "A fake jira addon"
                            "123"
                            nil)
            confluence-installed (p/create-install!
                                  (->Installs db)
                                  1
                                  "com.addon.adhesive.digital"
                                  "testkey"
                                  nil
                                  "testsharedsecret"
                                  "blah.atlassian.net"
                                  nil
                                  nil
                                  "confluence"
                                  "A fake jira addon"
                                  "123"
                                  nil)
            wrong-product-type (p/create-install!
                                (->Installs db)
                                1
                                "com.addon.adhesive.digital"
                                "testkey"
                                nil
                                "testsharedsecret"
                                "blah.atlassian.net"
                                nil
                                nil
                                "bamboohr"
                                "A fake jira addon"
                                "123"
                                nil)]
        (is (= jira-installed true))
        (is (= confluence-installed true))
        (is (= wrong-product-type nil))))))

(run-tests)
