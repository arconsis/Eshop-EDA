#!/bin/bash

# TODO: Read and parse the json from the env var CONNECTOR_JSON
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://debezium-service:8083/connectors/ --data "@connector.json"