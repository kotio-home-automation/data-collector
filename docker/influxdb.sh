#!/bin/sh
docker run --rm \
      -p 8086:8086 \
      -e INFLUXDB_DB=kotio \
      -e INFLUXDB_HTTP_AUTH_ENABLED=true \
      -e INFLUXDB_ADMIN_ENABLED=true \
      -e INFLUXDB_ADMIN_USER=admin -e INFLUXDB_ADMIN_PASSWORD=admin \
      -e INFLUXDB_USER=kotio -e INFLUXDB_USER_PASSWORD=kotio \
      -v $PWD/data:/var/lib/influxdb \
      influxdb:alpine
