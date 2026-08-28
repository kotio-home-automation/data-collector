(ns data-collector.dirigera-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [data-collector.dirigera :as dirigera]
            [data-collector.test-utils :as test-utils]))

(def light-data (json/parse-string "[{\"name\":\"Light 1\",\"level\":100,\"is_on\":true},{\"name\":\"Light 2\",\"level\":50,\"is_on\":false}]"))
(def parsed-light-data (dirigera/compose-dirigera-lights-readings light-data))

(def sensor-data (json/parse-string "[{\"name\":\"Sensor 1\",\"temperature\":21.5,\"humidity\":40},{\"name\":\"Sensor 2\",\"temperature\":21.5,\"humidity\":40,\"pm25\":10}]"))
(def parsed-sensor-data (dirigera/compose-dirigera-sensor-readings sensor-data))

(def magnet-data (json/parse-string "[{\"name\":\"Magnet 1\",\"is_open\":true,\"battery\":80}]"))
(def parsed-magnet-data (dirigera/compose-dirigera-magnet-readings magnet-data))

(def water-data (json/parse-string "[{\"name\":\"Water 1\",\"has_leak\":false,\"battery\":90}]"))
(def parsed-water-data (dirigera/compose-dirigera-water-sensor-readings water-data))

(deftest light-readings
  (testing "Parsed lights have correct number of items and values"
    (is (= 4 (count parsed-light-data)))
    (let [l1-level (first (filter #(and (= "light_level" (:measurement %)) (= "Light 1" (get-in % [:tags "name"]))) parsed-light-data))
          l1-on (first (filter #(and (= "light_is_on" (:measurement %)) (= "Light 1" (get-in % [:tags "name"]))) parsed-light-data))]
      (is (= 100 (get-in l1-level [:fields "value"])))
      (is (= 1 (get-in l1-on [:fields "value"]))))))

(deftest sensor-readings
  (testing "Parsed sensors have correct values and filter nil fields"
    ;; 2 sensors: Sensor 1 has temp, humidity (2 measurements). Sensor 2 has pm25, temperature, humidity (3 measurement). Total = 5
    (is (= 5 (count parsed-sensor-data)))
    (let [s1-temp (first (filter #(and (= "temperature" (:measurement %)) (= "Sensor 1" (get-in % [:tags "name"]))) parsed-sensor-data))
          s1-hum (first (filter #(and (= "humidity" (:measurement %)) (= "Sensor 1" (get-in % [:tags "name"]))) parsed-sensor-data))
          s2-pm25 (first (filter #(and (= "pm25" (:measurement %)) (= "Sensor 2" (get-in % [:tags "name"]))) parsed-sensor-data))]
      (is (= 21.5 (get-in s1-temp [:fields "value"])))
      (is (= 40.0 (get-in s1-hum [:fields "value"])))
      (is (= 10 (get-in s2-pm25 [:fields "value"]))))))

(deftest magnet-readings
  (testing "Parsed magnets have correct values"
    (is (= 2 (count parsed-magnet-data)))
    (let [m-open (first (filter #(= "magnet_is_open" (:measurement %)) parsed-magnet-data))
          m-batt (first (filter #(= "magnet_battery" (:measurement %)) parsed-magnet-data))]
      (is (= 1 (get-in m-open [:fields "value"])))
      (is (= 80 (get-in m-batt [:fields "value"]))))))

(deftest water-readings
  (testing "Parsed water sensors have correct values"
    (is (= 2 (count parsed-water-data)))
    (let [w-leak (first (filter #(= "water_has_leak" (:measurement %)) parsed-water-data))
          w-batt (first (filter #(= "water_battery" (:measurement %)) parsed-water-data))]
      (is (= 0 (get-in w-leak [:fields "value"])))
      (is (= 90 (get-in w-batt [:fields "value"]))))))
