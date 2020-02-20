(ns posterity.middleware
  (:require [cheshire.core :as json]
            [byte-streams :as bs]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]))

(defn bytestream->map
  [bytestream]
  (-> bytestream
      bs/to-string
      (json/parse-string csk/->kebab-case-keyword)))

(defn wrap-params [handler]
  (fn [request]
    (let [headers (->> request :headers (cske/transform-keys csk/->kebab-case-keyword))
          body (-> request :body bytestream->map)]
      (handler (assoc request :body body :headers headers)))))
