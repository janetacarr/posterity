(defproject posterity "0.1.0-SNAPSHOT"
  :description "Posterity is a Jira addon to convert Jira tickets into documentation."
  :url ""
  :license {:name "Propieitary"
            :url ""}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [aleph "0.4.6"]
                 [com.stuartsierra/component "0.3.2"]
                 [metosin/reitit "0.3.7"]
                 [compojure "1.6.1"]
                 [crypto-password "0.2.1"]
                 [honeysql "0.9.4"]
                 [nilenso/honeysql-postgres "0.2.5"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.postgresql/postgresql "42.2.5"]
                 [hiccup "1.0.5"]
                 [garden "1.3.9"]
                 [clj-json "0.5.3"]]
  :plugins [[cider/cider-nrepl "0.21.1"]]
  :main ^:skip-aot posterity.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
