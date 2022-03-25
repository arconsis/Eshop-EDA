variable "cluster_name" {
  description = "Kubernetes Cluster Name"
  default     = "eda-cluster"
}

variable "worker_sg_ids" {
  description = "Worker security group ids"
  type = list(string)
}

variable "subnet_ids" {
  description = "Subnet ids for eks"
  type = list(string)
}

variable "vpc_id" {
  description = "VPC id for eks"
  type = string
}

variable "policy_arn" {
  description = "ARN of additional worker policy"
  type = string
}