variable "msk_sg_ids" {
  description = "MSK Kafka security group ids"
  type        = list(string)
}

variable "subnet_ids" {
  description = "Subnet ids for MSK Kafka"
  type        = list(string)
}

variable "client_broker_encryption_in_transit" {
  description = "Encryption setting for data in transit between clients and brokers."
  type        = string
  default     = "TLS"
}

variable "in_cluster_encryption_encryption_in_transit" {
  description = "Whether data communication among broker nodes is encrypted"
  type        = bool
  default     = true
}