(ns data-collector.ruuvitag-test
  (:require [clojure.test :refer :all]
          [cheshire.core :as json]
          [data-collector.ruuvitag :as ruuvitag]
          [data-collector.test-utils :as test-utils]))

(def sensor-data (json/parse-string "{\"name\":\"Työhuone\",\"data\":{\"temperature\":21,\"pressure\":1001,\"humidity\":36}}"))

(def empty-data-set [])

(def data-set-of-one [sensor-data])

(def parsed-data (ruuvitag/parse-ruuvitag-sensor sensor-data))

(deftest parse-sensor-count
  (testing "Parsed data has three items"
    (is (= 3 (count parsed-data)))))

(deftest parse-single-entry-count
  (testing "Parsed data has one temperature, humidity and pressure reading"
    (is (= 1 (count (test-utils/measurement-filter "temperature" parsed-data))))
    (is (= 1 (count (test-utils/measurement-filter "humidity" parsed-data))))
    (is (= 1 (count (test-utils/measurement-filter "pressure"parsed-data))))))

(deftest parse-temperature-value
  (testing "Parsed data has correct name and temperature reading"
    (is (= "Työhuone" (get-in (first (test-utils/measurement-filter "temperature" parsed-data)) [:tags "name"])))
    (is (test-utils/equal-float 21.0 (get-in (first (test-utils/measurement-filter "temperature" parsed-data)) [:fields "value"])))))

(deftest parse-humidity-value
  (testing "Parsed data has correct name and humidity reading"
    (is (= "Työhuone" (get-in (first (test-utils/measurement-filter "humidity" parsed-data)) [:tags "name"])))
    (is (= 36 (get-in (first (test-utils/measurement-filter "humidity" parsed-data)) [:fields "value"])))))

(deftest parse-pressure-value
  (testing "Parsed data has correct name and pressure reading"
    (is (= "Työhuone" (get-in (first (test-utils/measurement-filter "pressure" parsed-data)) [:tags "name"])))
    (is (= 1001 (get-in (first (test-utils/measurement-filter "pressure" parsed-data)) [:fields "value"])))))

(deftest compose-ruuvitag-readings-empty
  (testing "Composing empty data set returns empty result"
    (is (empty? (ruuvitag/compose-ruuvitag-readings empty-data-set)))))

(deftest compose-ruuvitag-readings-single-entry
  (testing "Composing data set of one returns one result with three entries"
    (is (= 1 (count (ruuvitag/compose-ruuvitag-readings data-set-of-one))))
    (is (= 3 (count (first (ruuvitag/compose-ruuvitag-readings data-set-of-one)))))))
