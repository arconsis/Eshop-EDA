{
  "name": "database-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "plugin.name": "pgoutput",
    "database.hostname": "${database_hostname}",
    "database.port": "5432",
    "database.user": "${database_user}",
    "database.password": "${database_password}",
    "database.dbname" : "inventory",
    "database.server.name": "${database_name}",
    "table.whitelist": "public.inventory"
  }
}