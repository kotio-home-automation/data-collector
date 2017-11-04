(ns data-collector.core
  (:require [clj-http.client :as http])
  (:require [cheshire.core :as json])
  (:require [capacitor.core :as capasitor])
  (:gen-class))

(def db (capasitor/make-client {:db "kotio" :username "kotio" :password "kotio"}))

(defn parse-ruuvitag-sensor [sensor]
  (let [now (quot (System/currentTimeMillis) 1000)
    name {"name" (get sensor "name")}
    temperature {"value" (float (get-in sensor ["data" "temperature"]))}
    humidity {"value" (get-in sensor ["data" "humidity"])}
    pressure {"value" (get-in sensor ["data" "pressure"])}]
    [{:measurement "temperature" :tags name :fields temperature :timestamp now}
      {:measurement "humidity" :tags name :fields humidity :timestamp now}
      {:measurement "pressure" :tags name :fields pressure :timestamp now}]))

(defn parse-humidity [sensor]
  (let [humidity (int (read-string (get sensor "humidity")))]
    {"value" humidity}))

(defn parse-tellstick-sensor [sensor]
  (let [now (quot (System/currentTimeMillis) 1000)
    name {"name" (get sensor "name")}
    temperature {"value" (float (read-string (get sensor "temperature")))}]
    (if (nil? (get sensor "humidity"))
      [{:measurement "temperature" :tags name :fields temperature :timestamp now}]
      [
        {:measurement "temperature" :tags name :fields temperature :timestamp now}
        {:measurement "humidity" :tags name :fields (parse-humidity sensor) :timestamp now}
      ])))

(defn parse-switch-data [switch measurement-name]
  (let [now (quot (System/currentTimeMillis) 1000)
    name {"name" (get switch "name")}
    status {"switchedOn" (get switch "switchedOn")}]
  {:measurement measurement-name :tags name :fields status :timestamp now}))

(defn parse-tellstick-switch [switch]
  (parse-switch-data switch "switch"))

(defn parse-tellstick-switch-group [switch-group]
  (parse-switch-data switch-group "switchGroup"))

(defn compose-ruuvitag-readings [sensors]
  (map parse-ruuvitag-sensor sensors))

(defn compose-tellstick-sensor-readings [sensors]
  (map parse-tellstick-sensor sensors))

(defn compose-tellstick-switch-readings [switches]
  (concat
    (map parse-tellstick-switch (get switches "devices"))
    (map parse-tellstick-switch-group (get switches "groups"))))

(defn fetch-data [url]
  (try
    (get (http/get url) :body)
    (catch Exception e (println "Unable to fetch data from" url ":" (.getMessage e)))))

(defn save-data-points [data-points]
  (let [points (into [] data-points)]
    (try
      (if (> (count points) 0) (capasitor/write-points db points))
      (catch Exception e (println "Unable to write data:" e)))))

(defn -main []
  (let [ruuvitag-data (fetch-data "http://localhost:3102/ruuvitag")
      tellstick-sensor-data (fetch-data "http://localhost:3101/tellstick/sensors")
      tellstick-switch-data (fetch-data "http://localhost:3101/tellstick/switches")]
    (save-data-points (flatten (concat
      (compose-ruuvitag-readings (json/parse-string ruuvitag-data))
      (compose-tellstick-sensor-readings (json/parse-string tellstick-sensor-data))
      (compose-tellstick-switch-readings (json/parse-string tellstick-switch-data)))))))
