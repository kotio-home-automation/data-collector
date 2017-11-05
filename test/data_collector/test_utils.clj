(ns data-collector.test-utils)

(defn measurement-filter [measurement parsed-data]
  (filter #(= (get % :measurement) measurement) parsed-data))

(defn equal-float [x y]
  (<= (java.lang.Math/abs (- x y)) 0.1))
