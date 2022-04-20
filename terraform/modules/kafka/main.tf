resource "aws_cloudwatch_log_group" "msk_broker_logs" {
  name = "msk_broker_logs"
}

resource "aws_msk_cluster" "kafka" {
  cluster_name           = "kafka-eshop"
  kafka_version          = "2.8.1"
  number_of_broker_nodes = 2

  broker_node_group_info {
    instance_type   = "kafka.t3.small"
    ebs_volume_size = 1000
    client_subnets  = var.subnet_ids
    security_groups = var.msk_sg_ids
  }

  logging_info {
    broker_logs {
      cloudwatch_logs {
        enabled   = true
        log_group = aws_cloudwatch_log_group.msk_broker_logs.name
      }
    }
  }

  encryption_info {
    encryption_in_transit {
      client_broker = var.client_broker_encryption_in_transit
      in_cluster    = var.in_cluster_encryption_encryption_in_transit
    }
  }

  configuration_info {
    arn      = aws_msk_configuration.kafka_configuration.arn
    revision = aws_msk_configuration.kafka_configuration.latest_revision
  }
}

resource "aws_msk_configuration" "kafka_configuration" {
  kafka_versions = ["2.8.1"]
  name           = "mks-eda-configuration"

  server_properties = <<PROPERTIES
auto.create.topics.enable = false
delete.topic.enable = true
unclean.leader.election.enable = false
PROPERTIES
}