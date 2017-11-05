(ns data-collector.tellstick)

(defn- parse-humidity [sensor]
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

(defn- parse-tellstick-switch [switch]
  (parse-switch-data switch "switch"))

(defn- parse-tellstick-switch-group [switch-group]
  (parse-switch-data switch-group "switchGroup"))

(defn compose-tellstick-sensor-readings [sensors]
  (map parse-tellstick-sensor sensors))

(defn compose-tellstick-switch-readings [switches]
  (concat
    (map parse-tellstick-switch (get switches "devices"))
    (map parse-tellstick-switch-group (get switches "groups"))))
