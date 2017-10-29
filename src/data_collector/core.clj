(ns data-collector.core
  (:require [clj-http.client :as client])
  (:require [cheshire.core :as json])
  (:gen-class))

(def ruuvitag-data (transient []))

(defn -main
  "I don't do a whole lot... yet. Except print ruuvitag data!"
  []
  (dotimes [n 5]
    (let [response-body (get (client/get "http://localhost:3102/ruuvitag") :body)]
      (conj! ruuvitag-data (json/parse-string response-body))
    )
  )
  (println (json/generate-string (flatten (persistent! ruuvitag-data)) {:pretty true}))
)
