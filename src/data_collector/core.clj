(ns data-collector.core
  (:require [data-collector.tellstick :as tellstick]
          [data-collector.ruuvitag :as ruuvitag]
          [clj-http.client :as http]
          [cheshire.core :as json]
          [capacitor.core :as capasitor]
          [config.core :as conf]
          [overtone.at-at :as at])
  (:gen-class))

(def db (capasitor/make-client {:db (:db-name (conf/load-env)) :username (:db-username (conf/load-env)) :password (:db-password (conf/load-env))}))

(def my-pool (at/mk-pool))

(defn- fetch-data [url]
  (try
    (get (http/get url) :body)
    (catch Exception e (println "Unable to fetch data from" url ":" (.getMessage e)))))

(defn- save-data-points [data-points]
  (let [points (into [] data-points)]
    (try
      (if (> (count points) 0) (capasitor/write-points db points))
      (catch Exception e (println "Unable to write data:" e)))))

(defn- collect-data []
  (let [ruuvitag-data (fetch-data (:ruuvitag-url (conf/load-env)))
      tellstick-sensor-data (fetch-data (:tellstick-sensor-url (conf/load-env)))
      tellstick-switch-data (fetch-data (:tellstick-switch-url (conf/load-env)))]
    (save-data-points (flatten (concat
      (ruuvitag/compose-ruuvitag-readings (json/parse-string ruuvitag-data))
      (tellstick/compose-tellstick-sensor-readings (json/parse-string tellstick-sensor-data))
      (tellstick/compose-tellstick-switch-readings (json/parse-string tellstick-switch-data)))))))

(defn -main []
  (at/every (:collecting-period (conf/load-env)) collect-data my-pool))
