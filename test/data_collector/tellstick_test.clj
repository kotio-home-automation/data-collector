(ns data-collector.tellstick-test
  (:require [clojure.test :refer :all]
          [cheshire.core :as json]
          [data-collector.tellstick :as tellstick]
          [data-collector.test-utils :as test-utils]))

(def full-sensor-data (json/parse-string "{\"id\":123,\"name\":\"Olohuone\",\"temperature\":\"21.1\",\"humidity\":\"36\"}"))

(def parsed-full-sensor-data (tellstick/parse-tellstick-sensor full-sensor-data))

(def minimal-sensor-data (json/parse-string "{\"id\":234,\"name\":\"Parveke\",\"temperature\":\"8.1\"}"))

(def parsed-minimal-sensor-data (tellstick/parse-tellstick-sensor minimal-sensor-data))

(def minimal-switch-data (json/parse-string "{\"devices\":[{\"name\":\"TH lattiavalaisin\",\"id\":113,\"switchedOn\":false},{\"name\":\"Makuuhuone\",\"id\":115,\"switchedOn\":false}]}"))

(def full-switch-data (json/parse-string "{\"devices\":[{\"name\":\"TH lattiavalaisin\",\"id\":113,\"switchedOn\":false},{\"name\":\"Makuuhuone\",\"id\":115,\"switchedOn\":false}],\"groups\":[{\"name\":\"Olohuone kaikki\",\"id\":122,\"switchedOn\":false}]}"))

(def parsed-minimal-switch-data (tellstick/compose-tellstick-switch-readings minimal-switch-data))

(def parsed-full-switch-data (tellstick/compose-tellstick-switch-readings full-switch-data))

(deftest parse-sensor-count
  (testing "Parsed data has correct number of items"
    (is (= 2 (count parsed-full-sensor-data)))
    (is (= 1 (count parsed-minimal-sensor-data)))))

(deftest parse-sensor-name
  (testing "Parsed data has correct name"
    (is (= "Parveke" (get-in (first parsed-minimal-sensor-data) [:tags "name"])))))

(deftest parse-sensor-temperature
  (testing "Parsed data has correct temperature"
    (is (test-utils/equal-float 21.1 (get-in (first (test-utils/measurement-filter "temperature" parsed-full-sensor-data)) [:fields "value"])))))

(deftest parse-sensor-humidity
  (testing "Parsed data has correct humidity"
    (is (= 36 (get-in (first (test-utils/measurement-filter "humidity" parsed-full-sensor-data)) [:fields "value"])))))

(deftest parse-non-existing-sensor-humidity
  (testing "Parsed data doesn't have humidity when it's not in original input"
    (is (nil? (get-in (first (test-utils/measurement-filter "humidity" parsed-minimal-sensor-data)) [:fields "value"])))))

(deftest composed-switch-count
  (testing "Parsed data has correct number of items"
    (is (= 2 (count parsed-minimal-switch-data)))
    (is (= 3 (count parsed-full-switch-data)))))

(deftest composed-switch-name
  (testing "Switch has correct name"
    (is (= "TH lattiavalaisin" (get-in (first parsed-minimal-switch-data) [:tags "name"])))))

(deftest composed-switch-value
  (testing "Switch has correct value"
    (is (= false (get-in (first parsed-minimal-switch-data) [:fields "switchedOn"])))))
