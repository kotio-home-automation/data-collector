(ns data-collector.tellstick)

(defn parse-switch-data [switch measurement-name]
  (let [now (quot (System/currentTimeMillis) 1000)
    name {"name" (get switch "name")}
    status {"switchedOn" (get switch "switchedOn")}]
  {:measurement measurement-name :tags name :fields status :timestamp now}))

(defn- parse-tellstick-switch [switch]
  (parse-switch-data switch "switch"))

(defn- parse-tellstick-switch-group [switch-group]
  (parse-switch-data switch-group "switchGroup"))

(defn compose-tellstick-switch-readings [switches]
  (concat
    (map parse-tellstick-switch (get switches "devices"))
    (map parse-tellstick-switch-group (get switches "groups"))))
