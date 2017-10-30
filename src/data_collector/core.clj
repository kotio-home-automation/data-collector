(ns data-collector.core
  (:require [clj-http.client :as http])
  (:require [cheshire.core :as json])
  (:require [capacitor.core :as capasitor])
  (:gen-class))

(def db (capasitor/make-client {:db "kotio" :username "kotio" :password "kotio"}))

(defn write-sensor [sensor]
  (def now (quot (System/currentTimeMillis) 1000))
  (capasitor/write-points db
    [{:measurement "temperature" :tags {"name" (get sensor "name")} :fields {"value" (float (get-in sensor ["data" "temperature"]))} :timestamp now}
    {:measurement "humidity" :tags {"name" (get sensor "name")} :fields {"value" (get-in sensor ["data" "humidity"])} :timestamp now}
    {:measurement "pressure" :tags {"name" (get sensor "name")} :fields {"value" (get-in sensor ["data" "pressure"])} :timestamp now}]))

(defn save-readings [sensors]
  (dorun (map write-sensor sensors)))

(defn -main
  "Fetch ruuvitag data and write it to influxdb"
  []
  (let [response-body (get (http/get "http://pommi:3102/ruuvitag") :body)]
    (save-readings (json/parse-string response-body))))
