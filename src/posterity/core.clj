(ns posterity.core
  (:require [posterity.api.core :as api])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (api/start-server api/app 3000))
