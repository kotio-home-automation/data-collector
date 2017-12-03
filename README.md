# data-collector

Data collector for kotio sensors and switches.

Collects sensor and swich data from kotio REST API's and saves it to [influxdb](https://www.influxdata.com/time-series-platform/influxdb/).

Written in clojure.

## Development and installation requirements

Requires
* Java
* leiningen

## Installation

`lein uberjar`

## Usage requirements

Requires kotio REST API's for ruuvitag and tellstick, influxdb, Java.

## Usage

Copy [configuration file template](src/main/resources/config.edn) to your preferred location and adjust configuration settings as needed.

Run the jar with configuration file parameter `java -Dconfig=your-config-file -jar data-collector-0.1.0-standalone.jar``
## License

Copyright © 2017 Jori Lytter

Distributed under the MIT License.
