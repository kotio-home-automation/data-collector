(ns data-collector.core
  (:require [clj-http.client :as client])
  (:require [cheshire.core :as json])
  (:gen-class))

(defn -main
  "I don't do a whole lot... yet. Except print ruuvitag data!"
  []
  (let [response-body (get (client/get "http://localhost:3102/ruuvitag") :body)]
    (println (json/decode response-body))))
