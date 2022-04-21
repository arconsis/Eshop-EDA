package main

import (
	"bastion/models"
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"github.com/go-chi/chi/v5"
	"github.com/jackc/pgx/v4/pgxpool"
	"github.com/joho/godotenv"
	"golang.org/x/sync/errgroup"
	"io"
	"log"
	"net/http"
	"os"
	"time"
)

const debeziumHostKey = "DEBEZIUM_HOST"
const portKey = "PORT"
const databaseUrlKey = "DATABASE_URL"
const appEnvKey = "APP_ENV"
const securityProtocolEnvKey = "SECURITY_PROTOCOL"
const bootstrapServersKey = "BOOTSTRAP_SERVERS"

var debeziumHost = ""
var bootstrapServers = ""

func main() {

	if os.Getenv(appEnvKey) == "development" {
		err := godotenv.Load()
		if err != nil {
			log.Fatal("Error loading .env file")
		}
	}

	debeziumHost = os.Getenv(debeziumHostKey)
	bootstrapServers = os.Getenv(bootstrapServersKey)

	r := chi.NewRouter()

	r.Post("/bastion/databases", func(w http.ResponseWriter, r *http.Request) {
		var databases []models.Database
		err := parseJson(w, r, &databases)

		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		err = createDatabases(databases)

		if err != nil {
			log.Printf("Database creation failed: %v", err)
			http.Error(w, http.StatusText(400), 400)
			return
		}

		w.Write([]byte(fmt.Sprint("Databases created")))
	})

	r.Post("/bastion/connectors", func(w http.ResponseWriter, r *http.Request) {
		r.Body = http.MaxBytesReader(w, r.Body, 1048576)

		var connectors []models.Connector
		err := parseJson(w, r, &connectors)

		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		err = createConnectors(connectors)
		if err != nil {
			log.Printf("Connectors creation failed: %v", err)
			http.Error(w, http.StatusText(400), 400)
			return
		}
		w.Write([]byte(fmt.Sprint("Connectors created")))
	})

	r.Post("/bastion/topics", func(w http.ResponseWriter, r *http.Request) {
		var topics []models.Topic
		err := parseJson(w, r, &topics)

		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		err = createTopics(topics)

		if err != nil {
			log.Printf("Topics creation failed: %v", err)
			http.Error(w, http.StatusText(400), 400)
			return
		}

		w.Write([]byte(fmt.Sprint("Topics created")))
	})

	r.Delete("/bastion/topics/{topicName}", func(w http.ResponseWriter, r *http.Request) {

		topicName := chi.URLParam(r, "topicName")

		deleteTopic(topicName)

		w.Write([]byte(fmt.Sprint("Topic deleted")))
	})

	port := fmt.Sprintf(":%v", os.Getenv(portKey))
	log.Printf("Listening at server: %v", port)
	http.ListenAndServe(port, r)
}

func parseJson(w http.ResponseWriter, r *http.Request, dst interface{}) error {

	r.Body = http.MaxBytesReader(w, r.Body, 1048576)

	dec := json.NewDecoder(r.Body)
	dec.DisallowUnknownFields()

	// To improve error handling checkout https://www.alexedwards.net/blog/how-to-properly-parse-a-json-request-body
	return dec.Decode(&dst)
}

func createDatabases(databases []models.Database) error {
	dbpool, err := pgxpool.Connect(context.Background(), os.Getenv(databaseUrlKey))
	if err != nil {
		log.Printf("Unable to connect to database: %v\n\n", err)
		os.Exit(1)
	}

	defer dbpool.Close()

	for _, d := range databases {
		createDb := "CREATE DATABASE \"" + d.Name + "\" OWNER " + d.Owner

		_, err = dbpool.Exec(context.Background(), createDb)
		if err != nil {
			log.Printf("Create %v failed: %v\n", d.Name, err)
			return err
		}
	}

	return nil
}

func createConnectors(connectors []models.Connector) error {
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

func createConnector(connector models.Connector) error {
	b := new(bytes.Buffer)
	err := json.NewEncoder(b).Encode(connector)
	if err != nil {
		return err
	}

	res, err := http.Post(debeziumHost, "application/json", b)

	if err != nil {
		return err
	}

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

func deleteTopic(topicName string) error {
	securityProtocol := os.Getenv(securityProtocolEnvKey)
	adminClient, err := kafka.NewAdminClient(&kafka.ConfigMap{"bootstrap.servers": bootstrapServers, "security.protocol": securityProtocol})
	if err != nil {
		fmt.Printf("Failed to create Admin client: %s\n", err)
		return err
	}

	// Contexts are used to abort or limit the amount of time
	// the Admin call blocks waiting for a result.
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// Delete topic on cluster.
	// Set Admin options to wait for the operation to finish (or at most 60s)
	maxDur, err := time.ParseDuration("60s")
	if err != nil {
		return fmt.Errorf("Parse duration error")
	}

	results, err := adminClient.DeleteTopics(
		ctx,
		[]string{topicName},
		// Admin options
		kafka.SetAdminOperationTimeout(maxDur),
	)

	if err != nil {
		fmt.Printf("Failed to delete topic: %v\n", err)
		return err
	}

	for _, result := range results {
		fmt.Printf("%s\n", result)
	}

	adminClient.Close()
	return nil
}

func createTopics(topics []models.Topic) error {
	securityProtocol := os.Getenv(securityProtocolEnvKey)
	adminClient, err := kafka.NewAdminClient(&kafka.ConfigMap{"bootstrap.servers": bootstrapServers, "security.protocol": securityProtocol})
	if err != nil {
		fmt.Printf("Failed to create Admin client: %s\n", err)
		return err
	}

	// Contexts are used to abort or limit the amount of time
	// the Admin call blocks waiting for a result.
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// Create topics on cluster.
	// Set Admin options to wait for the operation to finish (or at most 60s)
	maxDur, err := time.ParseDuration("60s")
	if err != nil {
		return fmt.Errorf("Parse duration error")
	}

	var topicSpecifications []kafka.TopicSpecification

	for _, topic := range topics {
		topicSpecifications = append(topicSpecifications, kafka.TopicSpecification{
			Topic:             topic.Name,
			NumPartitions:     topic.Partitions,
			ReplicationFactor: topic.ReplicationFactor,
			Config:            topic.Config},
		)
	}

	results, err := adminClient.CreateTopics(
		ctx,
		topicSpecifications,
		// Admin options
		kafka.SetAdminOperationTimeout(maxDur),
	)

	if err != nil {
		fmt.Printf("Failed to create topics: %v\n", err)
		return err
	}

	for _, result := range results {
		fmt.Printf("%s\n", result)
	}

	adminClient.Close()
	return nil
}
