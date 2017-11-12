(defproject kotio-data-collector "0.1.0-SNAPSHOT"
  :description "Kotio home automation data collector."
  :url "https://github.com/jorilytter/kotio"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
    [clj-http "3.7.0"]
    [cheshire "5.8.0"]
    [capacitor "0.6.0"]
    [yogthos/config "0.9"]]
  :main ^:skip-aot data-collector.core
  :target-path "target/%s"
  :profiles {:dev {:resource-paths ["src/main/resources"]}
          :uberjar {:aot :all}})
