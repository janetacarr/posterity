(ns posterity.middleware-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [buddy.sign.jwt :as jwt]
            [posterity.middleware :as mid]))

(deftest get-identity-map-test
  (testing "JWT token"
    (let [client-key (str (java.util.UUID/randomUUID))
          account-id "testaccountid"
          test-claims {:iss client-key
                       :sub account-id}
          secret "blahblahblah"
          token (as-> test-claims $
                  (jwt/sign $ secret)
                  (str "JWT " $))
          identity-map (mid/get-identity-map token secret)]
      (is (= identity-map {:client-key client-key
                           :account-id account-id}))))
  (testing "jwt token"
    (let [client-key (str (java.util.UUID/randomUUID))
          account-id "testaccountid"
          test-claims {:iss client-key
                       :sub account-id}
          secret "blahblahblah"
          token (as-> test-claims $
                  (jwt/sign $ secret)
                  (str "jwt " $))
          identity-map (mid/get-identity-map token secret)]
      (is (= identity-map {:client-key client-key
                           :account-id account-id}))))
  (testing "plain token (no JWT or jwt)"
    (let [client-key (str (java.util.UUID/randomUUID))
          account-id "testaccountid"
          test-claims {:iss client-key
                       :sub account-id}
          secret "blahblahblah"
          token  (jwt/sign test-claims secret)
          identity-map (mid/get-identity-map token secret)]
      (is (= identity-map {:client-key client-key
                           :account-id account-id}))))
  (testing "Nil token"
    (let [client-key (str (java.util.UUID/randomUUID))
          account-id "testaccountid"
          test-claims {:iss client-key
                       :sub account-id}
          secret "blahblahblah"
          token  nil
          identity-map (mid/get-identity-map token secret)]
      (is (= identity-map nil))))
  (testing "Nil secret"
    (let [client-key (str (java.util.UUID/randomUUID))
          account-id "testaccountid"
          test-claims {:iss client-key
                       :sub account-id}
          secret nil
          token (jwt/sign test-claims secret)
          identity-map (mid/get-identity-map token secret)]
      (is (= identity-map nil)))))

(run-tests)
