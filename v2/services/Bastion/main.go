package main

import (
	"bytes"
	"log"
	"net/http"
	"os"
	"sync"
)

const ordersConnectorJsonKey = "ORDERS_CONNECTOR_JSON"
const warehouseConnectorJsonKey = "WAREHOUSE_CONNECTOR_JSON"
const paymentsConnectorJsonKey = "PAYMENTS_CONNECTOR_JSON"
const debeziumHostKey = "DEBEZIUM_HOST"

var debeziumHost = os.Getenv(debeziumHostKey)
var ordersConnectorJson = os.Getenv(ordersConnectorJsonKey)
var warehouseConnectorJson = os.Getenv(warehouseConnectorJsonKey)
var paymentsConnectorJson = os.Getenv(paymentsConnectorJsonKey)

func main() {
	var wg sync.WaitGroup

	wg.Add(3)
	go createConnector(ordersConnectorJson, &wg)
	go createConnector(warehouseConnectorJson, &wg)
	go createConnector(paymentsConnectorJson, &wg)

	log.Println("Main: Waiting for workers to finish")
	wg.Wait()
	log.Println("Main: Completed")
}

func createConnector(json string, wg *sync.WaitGroup) {
	defer wg.Done()

	body := bytes.NewBuffer([]byte(json))
	res, err := http.Post(debeziumHost, "application/json", body)

	if err != nil {
		log.Println("Error on http post")
	}

	defer res.Body.Close()
}
