(ns posterity.views.handlers.pages
  (:require [hiccup.core :as hiccup]
            [cheshire.core :as json]))

(def hello
  (str "<!DOCTYPE html>"
       (hiccup/html
        [:html {:lang "en"}
         [:head
          [:link {:rel "stylesheet" :href "https://unpkg.com/@atlaskit/css-reset@2.0.0/dist/bundle.css" :media "all"}]
          #_[:script {:src "https://connect-cdn.atl-paas.net/all.js" :async "async"}]
          "<script src=\"https://connect-cdn.atl-paas.net/all.js\" async></script>"]
         [:body
          [:section {:id "content" :class "ac-content"}
           [:h1 "Posterity Settings"]]]])))

(defn get-hello-page
  [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body hello})

(def confluence-configuration-page
  (str "<!DOCTYPE html>"
       (hiccup/html
        [:html {:lang "en"}
         [:head
          [:link {:rel "stylesheet" :href "https://unpkg.com/@atlaskit/css-reset@2.0.0/dist/bundle.css" :media "all"}]
          #_[:script {:src "https://connect-cdn.atl-paas.net/all.js" :async "async"}]
          "<script src=\"https://connect-cdn.atl-paas.net/all.js\" async></script>"]
         [:body
          [:section {:id "content" :class "ac-content"}
           [:h1 "Posterity Settings"]]]])))

(defn confluence-config-page
  [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body confluence-configuration-page})
