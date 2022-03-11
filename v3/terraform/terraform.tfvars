eda_database_name            = "postgres"
users_database_name          = "users-db"
users_history_topic          = "schema-changes.users"
users_table_include_list     = ["users"]
database_parameters          = [
  {
    name  = "rds.logical_replication"
    value = 1
    apply_method = "pending-reboot"
  }
]

