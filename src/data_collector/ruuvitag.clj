(ns data-collector.ruuvitag)

(defn parse-ruuvitag-sensor [sensor]
  (let [now (quot (System/currentTimeMillis) 1000)
    name {"name" (get sensor "name")}
    temperature {"value" (float (get-in sensor ["data" "temperature"]))}
    humidity {"value" (get-in sensor ["data" "humidity"])}
    pressure {"value" (get-in sensor ["data" "pressure"])}]
    [{:measurement "temperature" :tags name :fields temperature :timestamp now}
      {:measurement "humidity" :tags name :fields humidity :timestamp now}
      {:measurement "pressure" :tags name :fields pressure :timestamp now}]))

(defn compose-ruuvitag-readings [sensors]
  (map parse-ruuvitag-sensor sensors))
