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

output "bootstrap_servers" {
  value = module.kafka.bootstrap_brokers
}

# TODO: check connectors json format to make it valid, as now the output is not working when we try to create connectors
output "users_connector_json" {
  value = jsonencode(replace(data.template_file.users_connector_initializer.rendered, "\n", " "))
}

output "orders_connector_json" {
  value = jsonencode(replace(data.template_file.orders_connector_initializer.rendered, "\n", " "))
}

output "warehouse_connector_json" {
  value = jsonencode(replace(data.template_file.warehouse_connector_initializer.rendered, "\n", " "))
}

output "payments_connector_json" {
  value = jsonencode(replace(data.template_file.payment_connector_initializer.rendered, "\n", " "))
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

