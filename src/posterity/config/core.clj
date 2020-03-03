(ns posterity.config.core
  (:require [posterity.domain.protocols :as p]
            [posterity.config :refer [env]]))

(defrecord AtlassianConfig [config]
  p/AddonConfig
  (config-map [this]
    config))


;;FIXME: Make config map kebab case.
(defn jira-descriptor
  []
  (->AtlassianConfig {:name "Posterity"
                      :description "Posterity "
                      :key "com.adhesive-digital.posterity"
                      :base-url (str (:posterity-url env) "/jira")
                      :vendor {:name "Adhesive Digital"
                               :url "https://adhesive.digital"}
                      :authentication {:type "jwt"}
                      :lifecycle {:installed "/install"
                                  :uninstalled "/uninstall"
                                  :enabled "/enabled"
                                  :disabled "/disabled"}
                      :api-version 2
                      :modules {:general-pages [{:url "/settings.html"
                                                 :key "posterity-settings"
                                                 :location "system.top.navigation.bar"
                                                 :name {:value "Posterity"}}]
                                :configure-page {:url "/settings.html"
                                                 :key "posterity-configuration"
                                                 :name {:value "Posterity"}}
                                :webhooks [{:event "jira:issue_created"
                                            :url "/events"}
                                           {:event "jira:issue_updated"
                                            :url "/events"}]}
                      :scopes ["read"]}))

(defn confluence-descriptor
  []
  (->AtlassianConfig {:name "Posterity"
                      :description "Posterity "
                      :key "com.adhesive-digital.posterity"
                      :base-url (str (:posterity-url env) "/confluence")
                      :vendor {:name "Adhesive Digital"
                               :url "https://adhesive.digital"}
                      :authentication {:type "jwt"}
                      :lifecycle {:installed "/install"
                                  :uninstalled "/uninstall"
                                  :enabled "/enabled"
                                  :disabled "/disabled"}
                      :api-version 1
                      :modules {:general-pages [{:url "/settings.html"
                                                 :key "posterity-settings"
                                                 :location "system.top.navigation.bar"
                                                 :name {:value "Posterity"}}]
                                :configure-page {:url "/configure.html"
                                                 :key "posterity-configuration"
                                                 :name {:value "Posterity"}}}
                      :scopes ["read" "write"]}))
