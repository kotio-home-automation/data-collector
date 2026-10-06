(ns data-collector.tellstick-test
  (:require [clojure.test :refer :all]
          [cheshire.core :as json]
          [data-collector.tellstick :as tellstick]
          [data-collector.test-utils :as test-utils]))

(def minimal-switch-data (json/parse-string "{\"devices\":[{\"name\":\"TH lattiavalaisin\",\"id\":113,\"switchedOn\":false},{\"name\":\"Makuuhuone\",\"id\":115,\"switchedOn\":false}]}"))

(def full-switch-data (json/parse-string "{\"devices\":[{\"name\":\"TH lattiavalaisin\",\"id\":113,\"switchedOn\":false},{\"name\":\"Makuuhuone\",\"id\":115,\"switchedOn\":false}],\"groups\":[{\"name\":\"Olohuone kaikki\",\"id\":122,\"switchedOn\":false}]}"))

(def parsed-minimal-switch-data (tellstick/compose-tellstick-switch-readings minimal-switch-data))

(def parsed-full-switch-data (tellstick/compose-tellstick-switch-readings full-switch-data))

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
