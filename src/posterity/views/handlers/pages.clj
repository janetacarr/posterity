(ns posterity.views.handlers.pages
  (:require [hiccup.core :as hiccup]
            [cheshire.core :as json]
            [posterity.core.jira.configurator :refer [jira-configurator]]
            [posterity.domain.protocols :as p]))

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
           [:h1 "Posterity Settings"]
           [:h2 "Complete your setup:"]
           [:p "Go to confluence and enter this install key:"]
           [:p (str (java.util.UUID/randomUUID))]
           [:a {:href "https://adhesivedigital.atlassian.net/wiki/plugins/servlet/ac/com.adhesive-digital.posterity/posterity-configuration" :target "_blank"} "Confluence"]]]])))

(def install-other-app
  (str "<!DOCTYPE html>"
       (hiccup/html
        [:html {:lang "en"}
         [:head
          [:link {:rel "stylesheet" :href "https://unpkg.com/@atlaskit/css-reset@2.0.0/dist/bundle.css" :media "all"}]
          #_[:script {:src "https://connect-cdn.atl-paas.net/all.js" :async "async"}]
          "<script src=\"https://connect-cdn.atl-paas.net/all.js\" async></script>"]
         [:body
          [:section {:id "content" :class "ac-content"}
           [:h1 "Posterity Settings"]
           [:h2 "Complete your setup:"]
           [:p "You need to install the confluence companion add-on from the Atlassian Marketplace"]
           [:a {:href "https://marketplace.atlassian.com/search?hosting=cloud&product=confluence" :target "_blank"} "Go to Atlassian Marketplace"]]]])))

(defn jira-configuration-page
  [req]
  (let [jira-config (jira-configurator)
        client-key (get-in req [:identity :client-key])
        body (if (p/install-complete? jira-config client-key "jira")
               hello
               install-other-app)]
    {:status 200
     :headers {"content-type" "text/html"}
     :body body}))

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
           [:h1 "Posterity Settings"]
           [:p "Enter install key to complete setup: "]
           [:input {:type "password"}]]]])))

(defn confluence-config-page
  [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body confluence-configuration-page})
