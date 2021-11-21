package main

import (
	"bytes"
	"log"
	"net/http"
	"os"
)

// TODO: Add more keys for the all the connector configurations
const connectorJsonKey = "CONNECTOR_JSON"

func main() {

	connectorJson := os.Getenv(connectorJsonKey)

	body := bytes.NewBuffer([]byte(connectorJson))
	res, err := http.Post("http://debezium-service:8083/connectors/", "application/json", body)

	if err != nil {
		log.Fatal("Error on http post")
	}

	defer res.Body.Close()
}
