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
  default     = 3
}

variable "private_subnet_count" {
  type        = number
  description = "Private subnet count"
  default     = 3
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
}

variable "database_parameters" {
  description = "DB parameters"
  type        = list(map(string))
  default     = []
}
