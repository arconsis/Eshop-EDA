package main

import (
	"context"
	"flag"
	"fmt"
	"github.com/jackc/pgx/v4/pgxpool"
	"github.com/joho/godotenv"
	"log"
	"os"
)

const databaseUrlKey = "DATABASE_URL"
const appEnvKey = "APP_ENV"
const databaseAction = "databases"

func main() {

	if os.Getenv(appEnvKey) == "development" {
		err := godotenv.Load()
		if err != nil {
			log.Fatal("Error loading .env file")
		}
	}

	var action string
	flag.StringVar(&action, "action", "default", "action to execute")

	flag.Parse()

	switch action {
	case databaseAction:
		createDatabases()
	default:
		fmt.Println("No action provided")
	}
}

func createDatabases() {
	dbpool, err := pgxpool.Connect(context.Background(), os.Getenv(databaseUrlKey))
	if err != nil {
		fmt.Printf("Unable to connect to database: %v\n\n", err)
		os.Exit(1)
	}

	defer dbpool.Close()

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"orders-db\" OWNER postgres")
	if err != nil {
		fmt.Printf("Create orders-db failed: %v\n", err)
	}
	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"warehouse-db\" OWNER postgres")

	if err != nil {
		fmt.Printf("Create warehouse-db failed: %v\n", err)
	}

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"users-db\" OWNER postgres")
	if err != nil {
		fmt.Printf("Create users-db failed: %v\n", err)
	}

	_, err = dbpool.Exec(context.Background(), "CREATE DATABASE \"payments-db\" OWNER postgres")
	if err != nil {
		fmt.Printf("Create payments-db failed: %v\n", err)
	}

	if err == nil {
		fmt.Println("Create databases succeeded")
	}
}
