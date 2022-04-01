{
  "name": "${database_connector_name}",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "plugin.name": "pgoutput",
    "database.hostname": "${database_hostname}",
    "database.port": "5432",
    "database.user": "${database_user}",
    "database.password": "${database_password}",
    "database.dbname" : "${database_name}",
    "database.server.name": "${database_name}",
    "table.include.list": "${table_include_list}",
    "database.history.kafka.bootstrap.servers":"${bootstrap_servers}",
    "database.history.kafka.topic": "${history_topic}",
    "slot.name" : "${slot_name}"
  }
}