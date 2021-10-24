module "database" {
  source = "terraform-aws-modules/rds/aws"

  identifier = var.database_identifier

  engine                     = "postgres"
  engine_version             = "11.12"
  auto_minor_version_upgrade = false
  family                     = "postgres11" # DB parameter group
  major_engine_version       = "11"         # DB option group
  instance_class             = "db.t3.small"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_encrypted     = false

  # NOTE: Do NOT use 'user' as the value for 'username' as it throws:
  # "Error creating DB Instance: InvalidParameterValue: MasterUsername
  # user cannot be used as it is a reserved word used by the engine"
  name     = "postgres"
  username = var.database_username
  password = var.database_password
  port     = var.database_port

  multi_az               = true
  subnet_ids             = var.subnet_ids
  vpc_security_group_ids = var.security_group_ids

  maintenance_window              = "Mon:00:00-Mon:03:00"
  backup_window                   = "03:00-06:00"
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false

  performance_insights_enabled          = true
  performance_insights_retention_period = 7
  create_monitoring_role                = true
  monitoring_role_name                  = var.monitoring_role_name
  monitoring_interval                   = 60

  parameters = [
    {
      name  = "autovacuum"
      value = 1
    },
    {
      name  = "client_encoding"
      value = "utf8"
    },
    {
      name = "rds.logical_replication"
      value = "1"
      apply_method = "pending-reboot"
    }
  ]

  db_option_group_tags    = {
    "Sensitive" = "low"
  }
  db_parameter_group_tags = {
    "Sensitive" = "low"
  }
  db_subnet_group_tags    = {
    "Sensitive" = "high"
  }
}