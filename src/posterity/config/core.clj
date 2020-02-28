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
                      :baseUrl (:posterity-url env)
                      :vendor {:name "Adhesive Digital"
                               :url "https://adhesive.digital"}
                      :authentication {:type "jwt"}
                      :lifecycle {:installed "/jira/install"
                                  :uninstalled "/jira/uninstall"
                                  :enabled "/jira/enabled"
                                  :disabled "/jira/disabled"}
                      :apiVersion 1
                      :modules {:generalPages [{:url "/settings.html"
                                                :key "posterity-settings"
                                                :location "system.top.navigation.bar"
                                                :name {:value "Posterity"}}]
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
                      :baseUrl (:posterity-url env)
                      :vendor {:name "Adhesive Digital"
                               :url "https://adhesive.digital"}
                      :authentication {:type "jwt"}
                      :lifecycle {:installed "/confluence/install"
                                  :uninstalled "/confluence/uninstall"
                                  :enabled "/confluence/enabled"
                                  :disabled "/confluence/disabled"}
                      :apiVersion 1
                      :modules {:generalPages [{:url "/settings.html"
                                                :key "posterity-settings"
                                                :location "system.top.navigation.bar"
                                                :name {:value "Posterity"}}]}
                      :scopes ["read" "write"]}))
