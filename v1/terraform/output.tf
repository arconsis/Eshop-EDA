output "cluster_id" {
  description = "EKS cluster ID."
  value       = module.eks.cluster_id
}

output "cluster_endpoint" {
  description = "Endpoint for EKS control plane."
  value       = module.eks.cluster_endpoint
}

output "cluster_security_group_id" {
  description = "Security group ids attached to the cluster control plane."
  value       = module.eks.cluster_security_group_id
}

output "config_map_aws_auth" {
  description = "A kubernetes configuration to authenticate to this EKS cluster."
  value       = module.eks.config_map_aws_auth
}

output "cluster_name" {
  description = "Kubernetes Cluster Name"
  value       = var.cluster_name
}

output "zookeeper_connect_string" {
  value = module.kafka.zookeeper_connect_string
}

output "bootstrap_brokers_tls" {
  description = "TLS connection host:port pairs"
  value       = module.kafka.bootstrap_brokers_tls
}

output "bootstrap_brokers" {
  description = "list of one or more hostname:port pairs of kafka brokers"
  value       = module.kafka.bootstrap_brokers
}
