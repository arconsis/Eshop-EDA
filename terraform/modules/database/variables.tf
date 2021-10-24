variable "database_identifier" {
  description = "Database identifier"
}

variable "database_username" {
  description = "The password for the DB master"
  type        = string
  sensitive   = true
}

variable "database_password" {
  description = "The password for the DB master"
  type        = string
  sensitive   = true
}

variable "database_port" {
  description = "DB port"
  type        = number
  default = 5432
}

variable "subnet_ids" {
  description = "The ids of the subnets for the DB"
  type        = list(string)
}

variable "security_group_ids" {
  description = "Security group ids for the DB"
  type        = list(string)
}

variable "monitoring_role_name" {
  description = "Monitoring role name of the DB"
  type        = string
}