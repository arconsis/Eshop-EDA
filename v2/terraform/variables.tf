################################################################################
# General AWS Configuration
################################################################################
variable "aws_region" {
  description = "The AWS region things are created in"
  default     = "us-west-2"
}

################################################################################
# Network Configuration
################################################################################
variable "vpc_name" {
  description = "The name of the VPC. Other names will result from this."
  default     = "ms-vpc"
}

variable "create_vpc" {
  description = "Flag to define if we have to create vpc"
  type        = bool
  default     = true
}

variable "create_igw" {
  description = "Flag to define if we have to create IG"
  type        = bool
  default     = true
}

variable "single_nat_gateway" {
  description = "Flag to define if we need only one NAT GW"
  type        = bool
  default     = true
}

variable "enable_nat_gateway" {
  description = "Flag to define enable NAT GW"
  type        = bool
  default     = true
}

variable "cidr_block" {
  description = "Network IP range"
  default     = "192.168.0.0/16"
}

variable "availability_zones" {
  description = "List of availability zones you want. Example: eu-west-1a and eu-west-1b"
  default     = ["us-west-2a", "us-west-2b"]
}

variable "public_subnet_cidrs" {
  description = "List of public cidrs, for every availability zone you want you need one. Example: 10.0.0.0/24 and 10.0.1.0/24"
  default     = ["192.168.0.0/19", "192.168.32.0/19"]
}

variable "private_subnet_cidrs" {
  description = "List of private cidrs, for every availability zone you want you need one. Example: 10.0.0.0/24 and 10.0.1.0/24"
  default     = ["192.168.128.0/19", "192.168.160.0/19"]
}

variable "enable_dns_support" {
  description = "DNS support"
  default     = true
}

variable "enable_dns_hostnames" {
  description = "DNS hostnames"
  default     = true
}

################################################################################
# EKS Configuration
################################################################################
variable "cluster_name" {
  description = "Kubernetes Cluster Name"
  default     = "test-eks-cluster"
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
variable "users_database_username" {
  description = "The username for the users DB master"
  type        = string
  sensitive   = true
}

variable "users_database_password" {
  description = "The password for the users DB master"
  type        = string
  sensitive   = true
}

variable "users_database_name" {
  description = "users DB name"
  type        = string
}

variable "orders_database_username" {
  description = "The password for the orders DB master"
  type        = string
  sensitive   = true
}

variable "orders_database_password" {
  description = "The password for the orders DB master"
  type        = string
  sensitive   = true
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

variable "payments_database_username" {
  description = "The password for the payments DB master"
  type        = string
  sensitive   = true
}

variable "payments_database_password" {
  description = "The password for the payments DB master"
  type        = string
  sensitive   = true
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

variable "warehouse_database_username" {
  description = "The password for the warehouse DB master"
  type        = string
  sensitive   = true
}

variable "warehouse_database_password" {
  description = "The password for the warehouse DB master"
  type        = string
  sensitive   = true
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
