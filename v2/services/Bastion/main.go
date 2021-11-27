package main

import (
	"bytes"
	"context"
	"fmt"
	"github.com/go-chi/chi/v5"
	"github.com/jackc/pgx/v4/pgxpool"
	"github.com/joho/godotenv"
	"golang.org/x/sync/errgroup"
	"io"
	"log"
	"net/http"
	"os"
)

const ordersConnectorJsonKey = "ORDERS_CONNECTOR_JSON"
const warehouseConnectorJsonKey = "WAREHOUSE_CONNECTOR_JSON"
const paymentsConnectorJsonKey = "PAYMENTS_CONNECTOR_JSON"
const debeziumHostKey = "DEBEZIUM_HOST"
const portKey = "PORT"
const databaseUrlKey = "DATABASE_URL"
const appEnvKey = "APP_ENV"

var debeziumHost = ""
var ordersConnectorJson = ""
var warehouseConnectorJson = ""
var paymentsConnectorJson = ""

func main() {

	if os.Getenv(appEnvKey) != "production" {
		err := godotenv.Load()
		if err != nil {
			log.Fatal("Error loading .env file")
		}
	}

	debeziumHost = os.Getenv(debeziumHostKey)
	ordersConnectorJson = os.Getenv(ordersConnectorJsonKey)
	warehouseConnectorJson = os.Getenv(warehouseConnectorJsonKey)
	paymentsConnectorJson = os.Getenv(paymentsConnectorJsonKey)

	r := chi.NewRouter()
	r.Post("/createDatabases", func(w http.ResponseWriter, r *http.Request) {
		createDatabases()
		w.Write([]byte(fmt.Sprint("Databases created")))
	})

	r.Post("/createConnectors", func(w http.ResponseWriter, r *http.Request) {
		err := createConnectors()
		if err != nil {
			log.Printf("Connectors creation failed: %v", err)
			http.Error(w, http.StatusText(400), 400)
			return
		}
		w.Write([]byte(fmt.Sprint("Connectors created")))
	})

	port := fmt.Sprintf(":%v", os.Getenv(portKey))
	http.ListenAndServe(port, r)
}

func createConnectors() error {
	var eg errgroup.Group

	log.Println("createConnectors: Starting workers")
	eg.Go(func() error {
		return createConnector(ordersConnectorJson)
	})

	eg.Go(func() error {
		return createConnector(warehouseConnectorJson)
	})

	eg.Go(func() error {
		return createConnector(paymentsConnectorJson)
	})

	log.Println("createConnectors: Waiting for workers to finish")
	err := eg.Wait()
	return err
}

func createDatabases() {
	dbpool, err := pgxpool.Connect(context.Background(), os.Getenv(databaseUrlKey))
	if err != nil {
		log.Printf("Unable to connect to database: %v\n\n", err)
		os.Exit(1)
	}

	defer dbpool.Close()

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"orders-db\" OWNER postgres")
	if err != nil {
		log.Printf("Create orders-db failed: %v\n", err)
	}
	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"warehouse-db\" OWNER postgres")

	if err != nil {
		log.Printf("Create warehouse-db failed: %v\n", err)
	}

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"user-db\" OWNER postgres")
	if err != nil {
		log.Printf("Create user-db failed: %v\n", err)
	}

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"payments-db\" OWNER postgres")
	if err != nil {
		log.Printf("Create payments-db failed: %v\n", err)
	}
}

func createConnector(json string) error {
	body := bytes.NewBuffer([]byte(json))
	res, err := http.Post(debeziumHost, "application/json", body)
	defer res.Body.Close()

	if res.StatusCode > 299 {
		if res.Body != nil {
			bodyBytes, err := io.ReadAll(res.Body)
			if err != nil {
				log.Fatal(err)
			}
			bodyString := string(bodyBytes)

			return fmt.Errorf("debezium connector error with status: %v response: %v", res.StatusCode, bodyString)
		}

		return fmt.Errorf("debezium connector error with status: %v", res.StatusCode)
	}

	return err
}
