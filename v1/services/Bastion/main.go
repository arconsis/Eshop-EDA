package main

import (
	"context"
	"fmt"
	"github.com/go-chi/chi/v5"
	"github.com/jackc/pgx/v4/pgxpool"
	"github.com/joho/godotenv"
	"log"
	"net/http"
	"os"
)

const portKey = "PORT"
const databaseUrlKey = "DATABASE_URL"
const appEnvKey = "APP_ENV"
const databaseUsername = "DATABASE_USERNAME"

func main() {
	if os.Getenv(appEnvKey) == "development" {
		err := godotenv.Load()
		if err != nil {
			log.Fatal("Error loading .env file")
		}
	}

	r := chi.NewRouter()
	r.Post("/bastion/createDatabases", func(w http.ResponseWriter, r *http.Request) {
		createDatabases()
		w.Write([]byte(fmt.Sprint("Databases created")))
	})

	port := fmt.Sprintf(":%v", os.Getenv(portKey))
	log.Printf("Listeing on port: %v\n", port)
	http.ListenAndServe(port, r)
}

func createDatabases() {
	dbUsename := os.Getenv(databaseUsername)
	dbpool, err := pgxpool.Connect(context.Background(), os.Getenv(databaseUrlKey))
	if err != nil {
		log.Printf("Unable to connect to database: %v\n\n", err)
		os.Exit(1)
	}
	defer dbpool.Close()
	createOrdersDb := "CREATE DATABASE \"orders-db\" OWNER " + dbUsename
	createWarehouseDb := "CREATE DATABASE \"warehouse-db\" OWNER " + dbUsename
	createUsersDb := "CREATE DATABASE \"users-db\" OWNER " + dbUsename
	createPaymentsDb := "CREATE DATABASE \"payments-db\" OWNER " + dbUsename
	_, err = dbpool.Exec(context.Background(), createOrdersDb)
	if err != nil {
		log.Printf("Create orders-db failed: %v\n", err)
	}
	_, err = dbpool.Exec(context.Background(), createWarehouseDb)

	if err != nil {
		log.Printf("Create warehouse-db failed: %v\n", err)
	}

	_, err = dbpool.Exec(context.Background(), createUsersDb)
	if err != nil {
		log.Printf("Create users-db failed: %v\n", err)
	}

	_, err = dbpool.Exec(context.Background(), createPaymentsDb)
	if err != nil {
		log.Printf("Create payments-db failed: %v\n", err)
	}
}
