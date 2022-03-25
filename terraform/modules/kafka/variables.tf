variable "msk_sg_ids" {
  description = "MSK Kafka security group ids"
  type        = list(string)
}

variable "subnet_ids" {
  description = "Subnet ids for MSK Kafka"
  type        = list(string)
}