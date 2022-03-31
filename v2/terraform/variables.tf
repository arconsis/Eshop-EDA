################################################################################
# General AWS Configuration
################################################################################
variable "aws_region" {
  description = "The AWS region things are created in"
  default     = "eu-west-1"
}

variable "aws_profile" {
  description = "The AWS profile name"
  default     = "arconsis"
}

variable "default_tags" {
  description = "Default tags to set to every resource"
  type        = map(string)
  default     = {
    Project     = "eda-v2"
    ManagedBy   = "terraform"
    Environment = "stg"
  }
}

################################################################################
# Network Configuration
################################################################################
################################################################################
# Network Configuration
################################################################################
variable "vpc_name" {
  description = "The name of the VPC. Other names will result from this."
  default     = "ms-vpc"
}

variable "public_subnet_count" {
  type        = number
  description = "Public subnet count"
  default     = 2
}

variable "private_subnet_count" {
  type        = number
  description = "Private subnet count"
  default     = 2
}

variable "cidr_block" {
  description = "Network IP range"
  default     = "10.0.0.0/16"
}

################################################################################
# EKS Configuration
################################################################################
variable "cluster_name" {
  description = "Kubernetes Cluster Name"
  default     = "eda-cluster"
}

################################################################################
# Project metadata
################################################################################
variable "environment" {
  description = "Indicate the environment"
  default     = "stg"
}

################################################################################
# Database Configuration
################################################################################
# https://blog.gruntwork.io/a-comprehensive-guide-to-managing-secrets-in-your-terraform-code-1d586955ace1
# using environment variables
variable "eda_database_username" {
  description = "The password for the eda DB master"
  type        = string
  sensitive   = true
}

variable "eda_database_password" {
  description = "The password for the eda DB master"
  type        = string
  sensitive   = true
}

variable "eda_database_name" {
  description = "eda DB name"
  type        = string
  default = "postgres"
}

variable "users_database_name" {
  description = "users DB name"
  type        = string
}

variable "users_history_topic" {
  description = "users db changes topic"
  type        = string
}

variable "users_table_include_list" {
  description = "list of tables that should be observed by debezium"
  type        = list(string)
}

variable "users_slot_name" {
  description = "users slot name observed by debezium"
  type        = string
}

variable "email_database_name" {
  description = "email DB name"
  type        = string
}

variable "orders_database_name" {
  description = "orders DB name"
  type        = string
}

variable "orders_history_topic" {
  description = "orders db changes topic"
  type        = string
}

variable "orders_table_include_list" {
  description = "list of tables that should be observed by debezium"
  type        = list(string)
}

variable "orders_slot_name" {
  description = "orders slot name observed by debezium"
  type        = string
}

variable "payments_history_topic" {
  description = "payments db changes topic"
  type        = string
}

variable "payments_table_include_list" {
  description = "list of tables that should be observed by debezium"
  type        = list(string)
}

variable "payments_database_name" {
  description = "payments DB name"
  type        = string
}

variable "payments_slot_name" {
  description = "payments slot name observed by debezium"
  type        = string
}

variable "warehouse_database_name" {
  description = "warehouse DB name"
  type        = string
}

variable "warehouse_history_topic" {
  description = "orders db changes topic"
  type        = string
}

variable "warehouse_table_include_list" {
  description = "list of tables that should be observed by debezium"
  type        = list(string)
}

variable "warehouse_slot_name" {
  description = "warehouse slot name observed by debezium"
  type        = string
}

variable "database_parameters" {
  description = "DB parameters"
  type        = list(map(string))
  default     = []
}
