(ns data-collector.ruuvitag)

(defn parse-pressure [sensor]
    (let [pressure (float (get-in sensor ["data" "pressure"]))]
      {"value" pressure}))

(defn parse-ruuvitag-sensor [sensor]
  (let [now (quot (System/currentTimeMillis) 1000)
    name {"name" (get sensor "name")}
    temperature {"value" (float (get-in sensor ["data" "temperature"]))}
    humidity {"value" (float (get-in sensor ["data" "humidity"]))}]
    (if (nil? (get-in sensor ["data" "pressure"]))
      [
        {:measurement "temperature" :tags name :fields temperature :timestamp now}
        {:measurement "humidity" :tags name :fields humidity :timestamp now}
      ]
      [
        {:measurement "temperature" :tags name :fields temperature :timestamp now}
        {:measurement "humidity" :tags name :fields humidity :timestamp now}
        {:measurement "pressure" :tags name :fields (parse-pressure sensor) :timestamp now}
      ])))

(defn compose-ruuvitag-readings [sensors]
  (map parse-ruuvitag-sensor sensors))
