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

func main() {

	if os.Getenv(appEnvKey) != "production" {
		err := godotenv.Load()
		if err != nil {
			log.Fatal("Error loading .env file")
		}
	}

	r := chi.NewRouter()
	r.Post("/createDatabases", func(w http.ResponseWriter, r *http.Request) {
		createDatabases()
		w.Write([]byte(fmt.Sprint("Databases created")))
	})

	port := fmt.Sprintf(":%v", os.Getenv(portKey))
	http.ListenAndServe(port, r)
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

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"users-db\" OWNER postgres")
	if err != nil {
		log.Printf("Create users-db failed: %v\n", err)
	}

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"payments-db\" OWNER postgres")
	if err != nil {
		log.Printf("Create payments-db failed: %v\n", err)
	}
}
