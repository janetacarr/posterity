(ns posterity.config.core
  (:require [posterity.domain.protocols :as p]))

(defrecord Atlassian-config [config]
  p/addon-config
  (config-map [this]
    config))

(def atlassian-descriptor
  (->Atlassian-config {:name "Hello World"
                       :description "Atlassian connect app"
                       :key "com.example.myapp"
                       :baseUrl "https://ae45742c.ngrok.io"
                       :vendor {:name "Example, Inc"
                                :url "http://example.com"}
                       :authentication {:type "none"}
                       :apiVersion 1
                       :modules {:generalPages [{:url "/helloworld.html"
                                                 :key "hello-world"
                                                 :location "system.top.navigation.bar"
                                                 :name {:value "Greeting"}}]}}))
