eda_database_name               = "postgres"
users_database_name             = "usersDb"
users_history_topic             = "schema-changes.users"
users_table_include_list        = ["users"]
users_slot_name                 = "users_slot"
users_outbox_table_include_list = ["users_outbox_events"]
database_parameters             = [
  {
    name         = "rds.logical_replication"
    value        = 1
    apply_method = "pending-reboot"
  }
]
