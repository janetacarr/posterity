(defproject posterity "0.1.0-SNAPSHOT"
  :description "Posterity is a Jira addon to convert Jira tickets into documentation."
  :url ""
  :license {:name "Propieitary"
            :url ""}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.6.0"]
                 [org.clojure/core.async "0.7.559"]
                 [aleph "0.4.6"]
                 [byte-streams "0.2.4"]
                 [cprop "0.1.15"]
                 [metosin/reitit "0.3.7"]
                 [compojure "1.6.1"]
                 [crypto-password "0.2.1"]
                 [honeysql "0.9.4"]
                 [nilenso/honeysql-postgres "0.2.5"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.postgresql/postgresql "42.2.5"]
                 [mount "0.1.16"]
                 [manifold "0.1.8"]
                 [hiccup "1.0.5"]
                 [garden "1.3.9"]
                 [cheshire "5.10.0"]
                 [camel-snake-kebab "0.4.1"]]
  :plugins [[cider/cider-nrepl "0.21.1"]]
  :main ^:skip-aot posterity.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :uberjar-name "posterity.jar"}
             :dev [:project/dev]
             :project/dev {:jvm-opts ["-Dconf=dev-config.edn"]}})
