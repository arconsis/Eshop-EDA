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
    Project   = "eshop-eda"
    ManagedBy = "terraform"
  }
}

variable "repositories" {
  description = "Defines the repositories to create"
  type        = set(string)
  default     = [
    "v1/bastion",
    "v1/warehouse",
    "v1/orders",
    "v1/users",
    "v1/email",
    "v1/payments",
    "v2/bastion",
    "v2/warehouse",
    "v2/orders",
    "v2/users",
    "v2/email",
    "v2/payments",
    "v3/bastion",
    "v3/warehouse",
    "v3/orders",
    "v3/users",
    "v3/email",
    "v3/payments",
  ]
}