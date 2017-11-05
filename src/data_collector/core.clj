(ns data-collector.core
  (:require [data-collector.tellstick :as tellstick]
          [data-collector.ruuvitag :as ruuvitag]
          [clj-http.client :as http]
          [cheshire.core :as json]
          [capacitor.core :as capasitor])
  (:gen-class))

(def db (capasitor/make-client {:db "kotio" :username "kotio" :password "kotio"}))

(defn- fetch-data [url]
  (try
    (get (http/get url) :body)
    (catch Exception e (println "Unable to fetch data from" url ":" (.getMessage e)))))

(defn- save-data-points [data-points]
  (let [points (into [] data-points)]
    (try
      (if (> (count points) 0) (capasitor/write-points db points))
      (catch Exception e (println "Unable to write data:" e)))))

(defn -main []
  (let [ruuvitag-data (fetch-data "http://localhost:3102/ruuvitag")
      tellstick-sensor-data (fetch-data "http://localhost:3101/tellstick/sensors")
      tellstick-switch-data (fetch-data "http://localhost:3101/tellstick/switches")]
    (save-data-points (flatten (concat
      (ruuvitag/compose-ruuvitag-readings (json/parse-string ruuvitag-data))
      (tellstick/compose-tellstick-sensor-readings (json/parse-string tellstick-sensor-data))
      (tellstick/compose-tellstick-switch-readings (json/parse-string tellstick-switch-data)))))))
