(ns posterity.settings.token
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as budmid]
            [camel-snake-kebab.core :as csk]
            [cheshire.core :as json]
            [posterity.db.core :refer [new-db-spec]]
            [posterity.db.installs :refer [->Installs]]
            [posterity.db.install-keys :refer [->InstallKeys]]
            [posterity.domain.protocols :as p]))


(defrecord AuthToken [installer product-type]
  p/TokenValidator
  (get-secret-bytes [this token]
    (letfn [(decode [b64-str]
              (-> (java.util.Base64/getDecoder)
                  (.decode b64-str)))]
      (let [issuer (some-> token
                           (clojure.string/split #"[.]")
                           (second)
                           (decode)
                           (String.)
                           (json/parse-string csk/->kebab-case-keyword)
                           (:iss))
            shared-secret (:shared-secret (p/get-install!
                                           installer
                                           issuer
                                           product-type))
            secret-bytes (->> shared-secret
                              (map (comp byte int))
                              (byte-array)
                              (bytes))]
        secret-bytes))))

(defn auth-token-installs
  [product-type]
  (let [installer (->Installs (new-db-spec {}))
        auther (->AuthToken installer product-type)]
    auther))

(defn auth-token-install-key
  [product-type]
  (let [installer (->InstallKeys (new-db-spec {}))
        auther (->AuthToken installer product-type)]
    auther))
