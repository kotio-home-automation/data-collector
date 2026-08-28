(ns data-collector.dirigera)

(defn- get-now []
  (quot (System/currentTimeMillis) 1000))

(defn parse-light [light]
  (let [now (get-now)
        name {"name" (get light "name")}
        fields {"light_level" (get light "level")
                "light_is_on" (if (contains? light "is_on") (if (get light "is_on") 1 0) nil)}]
    (for [[k v] fields :when (some? v)]
      {:measurement k :tags name :fields {"value" v} :timestamp now})))

(defn parse-sensor [sensor]
  (let [now (get-now)
        name {"name" (get sensor "name")}
        fields {"temperature" (float (get sensor "temperature"))
                "humidity" (float (get sensor "humidity"))
                "pm25" (get sensor "pm25")
                "voc" (get sensor "voc")
                "co2" (get sensor "co2")}]
    (for [[k v] fields :when (some? v)]
      {:measurement k :tags name :fields {"value" v} :timestamp now})))

(defn parse-magnet [magnet]
  (let [now (get-now)
        name {"name" (get magnet "name")}
        fields {"magnet_is_open" (if (contains? magnet "is_open") (if (get magnet "is_open") 1 0) nil)
                "magnet_battery" (get magnet "battery")}]
    (for [[k v] fields :when (some? v)]
      {:measurement k :tags name :fields {"value" v} :timestamp now})))

(defn parse-water-sensor [ws]
  (let [now (get-now)
        name {"name" (get ws "name")}
        fields {"water_has_leak" (if (contains? ws "has_leak") (if (get ws "has_leak") 1 0) nil)
                "water_battery" (get ws "battery")}]
    (for [[k v] fields :when (some? v)]
      {:measurement k :tags name :fields {"value" v} :timestamp now})))

(defn compose-dirigera-lights-readings [lights]
  (mapcat parse-light lights)
  )

(defn compose-dirigera-sensor-readings [sensors]
  (mapcat parse-sensor sensors))

(defn compose-dirigera-magnet-readings [magnets]
  (mapcat parse-magnet magnets))

(defn compose-dirigera-water-sensor-readings [sensors]
  (mapcat parse-water-sensor sensors))
