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

variable "instance_type" {
  description = "instance type of workers"
  default = "t2.medium"
  type = string
}

variable "workers_min_size" {
  description = "min size of workers"
  default = 3
  type = number
}

variable "workers_max_size" {
  description = "max size of workers"
  default = 8
  type = number
}

variable "workers_desired_size" {
  description = "desired size of workers"
  default = 3
  type = number
}
