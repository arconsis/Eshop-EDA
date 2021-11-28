eda_database_name            = "postgres"
users_database_name          = "users-db"
users_history_topic          = "schema-changes.users"
users_table_include_list     = ["users"]
orders_database_name         = "orders-db"
orders_history_topic         = "schema-changes.orders"
orders_table_include_list    = ["orders"]
warehouse_database_name      = "warehouse-db"
warehouse_history_topic      = "schema-changes.warehouse"
warehouse_table_include_list = ["shipments", "inventory"]
payments_database_name       = "orders-db"
payments_history_topic       = "schema-changes.payments"
payments_table_include_list  = ["payments"]
database_parameters          = [
  {
    name  = "rds_replication"
    value = 1
  }
]

