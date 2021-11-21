curl -H 'Content-Type: application/json' localhost:8083/connectors --data '
{
  "name": "orders-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "plugin.name": "pgoutput",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "secret",
    "database.dbname" : "orders-db",
    "database.server.name": "postgres",
    "table.include.list": "public.orders"
  }
}'