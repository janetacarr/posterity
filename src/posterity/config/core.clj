(ns posterity.config.core
  (:require [posterity.domain.protocols :as p]))

(defrecord AtlassianConfig [config]
  p/AddonConfig
  (config-map [this]
    config))

(def atlassian-descriptor
  (->AtlassianConfig {:name "Posterity"
                      :description "Posterity "
                      :key "com.adhesive-digital.posterity"
                      :baseUrl "https://34186933.ngrok.io"
                      :vendor {:name "Adhesive Digital"
                               :url "https://adhesive.digital"}
                      :authentication {:type "jwt"}
                      :lifecycle {:installed "/jira/install"}
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
