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

const usersConnectorJsonKey = "USERS_CONNECTOR_JSON"
const debeziumHostKey = "DEBEZIUM_HOST"
const portKey = "PORT"
const databaseUrlKey = "DATABASE_URL"
const appEnvKey = "APP_ENV"

var debeziumHost = ""

func main() {

	if os.Getenv(appEnvKey) == "development" {
		err := godotenv.Load()
		if err != nil {
			log.Fatal("Error loading .env file")
		}
	}

	debeziumHost = os.Getenv(debeziumHostKey)

	connectors := []string{os.Getenv(usersConnectorJsonKey)}
	log.Println("connectors...", connectors)
	log.Println("debeziumHost...", debeziumHost)
	r := chi.NewRouter()
	r.Post("/bastion/createDatabases", func(w http.ResponseWriter, r *http.Request) {
		log.Println("Request to create databases")
		createDatabases()
		w.Write([]byte(fmt.Sprint("Databases created")))
	})

	r.Post("/bastion/createConnectors", func(w http.ResponseWriter, r *http.Request) {
		log.Println("Request to create connectors")
		err := createConnectors(connectors)
		if err != nil {
			log.Printf("Connectors creation failed: %v", err)
			http.Error(w, http.StatusText(400), 400)
			return
		}
		w.Write([]byte(fmt.Sprint("Connectors created")))
	})

	port := fmt.Sprintf(":%v", os.Getenv(portKey))
	log.Println("Listening on port port: ", port)
	http.ListenAndServe(port, r)
}

func createConnectors(connectors []string) error {
	var eg errgroup.Group

	log.Println("createConnectors: Starting workers")

	for _, c := range connectors {
		connector := c
		eg.Go(func() error {
			return createConnector(connector)
		})
	}

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

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"users-db\" OWNER postgres")
	if err != nil {
		log.Printf("Create users-db failed: %v\n", err)
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
