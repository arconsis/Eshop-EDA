eda_database_name                   = "postgres"
users_database_name                 = "users-db"
users_history_topic                 = "schema-changes.users"
users_table_include_list            = ["users"]
users_outbox_table_include_list     = ["users_outbox_events"]
users_slot_name                     = "users_slot"
orders_database_name                = "orders-db"
orders_history_topic                = "schema-changes.orders"
orders_table_include_list           = ["orders"]
orders_outbox_table_include_list    = ["orders_outbox_events"]
orders_slot_name                    = "orders_slot"
warehouse_database_name             = "warehouse-db"
warehouse_history_topic             = "schema-changes.warehouse"
warehouse_table_include_list        = ["shipments", "inventory"]
warehouse_outbox_table_include_list = ["warehouse_outbox_events"]
warehouse_slot_name                 = "warehouse_slot"
payments_database_name              = "orders-db"
payments_history_topic              = "schema-changes.payments"
payments_table_include_list         = ["payments"]
payments_outbox_table_include_list  = ["payments_outbox_events"]
payments_slot_name                  = "payments_slot"
email_database_name                 = "email-db"
database_parameters                 = [
  {
    name         = "rds.logical_replication"
    value        = 1
    apply_method = "pending-reboot"
  }
]

