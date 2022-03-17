output "zookeeper_connect_string" {
  value = aws_msk_cluster.kafka.zookeeper_connect_string
}

output "bootstrap_brokers_tls" {
  description = "TLS connection host:port pairs"
  value       = aws_msk_cluster.kafka.bootstrap_brokers_tls
}

output "bootstrap_brokers" {
  description = "list of one or more hostname:port pairs of kafka brokers"
  value       = aws_msk_cluster.kafka.bootstrap_brokers
}

