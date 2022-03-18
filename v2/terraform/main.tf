provider "aws" {
  shared_credentials_files = ["$HOME/.aws/credentials"]
  profile                  = var.aws_profile
  region                   = var.aws_region
  default_tags {
    tags = var.default_tags
  }
}

################################################################################
# VPC Configuration
################################################################################
module "networking" {
  source                        = "../../terraform/modules/network"
  region                        = var.aws_region
  vpc_name                      = var.vpc_name
  vpc_cidr                      = var.cidr_block
  private_subnet_count          = var.private_subnet_count
  public_subnet_count           = var.public_subnet_count
  public_subnet_additional_tags = {
    "kubernetes.io/role/elb"                    = "1"
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
  }
  private_subnet_additional_tags = {
    "kubernetes.io/role/internal-elb"           = "1"
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
  }
}

################################################################################
# SG Configuration
################################################################################
# TODO: Create different SGs for DB and Kafka
module "private_vpc_sg" {
  source            = "../../terraform/modules/security"
  sg_name           = "private-vpc-security-group"
  description       = "Controls access to the private database (not internet facing) and MSK cluster"
  vpc_id            = module.networking.vpc_id
  egress_cidr_rules = {
    1 = {
      description      = "allow all outbound"
      protocol         = "-1"
      from_port        = 0
      to_port          = 0
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = ["::/0"]
    }
  }
  egress_source_sg_rules  = {}
  ingress_source_sg_rules = {}
  ingress_cidr_rules      = {
    1 = {
      description      = "allow inbound access only from resources in VPC"
      protocol         = "tcp"
      from_port        = 0
      to_port          = 0
      cidr_blocks      = [module.networking.vpc_cidr_block]
      ipv6_cidr_blocks = [module.networking.vpc_ipv6_cidr_block]
    }
  }
}

module "eks_worker_sg" {
  source                  = "../../terraform/modules/security"
  sg_name                 = "eks-worker-group-mgmt"
  description             = "worker group mgmt"
  vpc_id                  = module.networking.vpc_id
  egress_cidr_rules       = {}
  egress_source_sg_rules  = {}
  ingress_source_sg_rules = {}
  ingress_cidr_rules      = {
    1 = {
      description      = "allow inbound access only from resources in VPC"
      protocol         = "tcp"
      from_port        = 22
      to_port          = 22
      cidr_blocks      = [module.networking.vpc_cidr_block]
      ipv6_cidr_blocks = [module.networking.vpc_ipv6_cidr_block]
    }
  }
}

################################################################################
# Database Configuration
################################################################################
# Eda Database
module "eda_database" {
  source               = "../../terraform/modules/database"
  database_identifier  = "eda-database"
  database_name        = var.eda_database_name
  database_username    = var.eda_database_username
  database_password    = var.eda_database_password
  subnet_ids           = module.networking.private_subnet_ids
  security_group_ids   = [module.private_vpc_sg.security_group_id]
  monitoring_role_name = "EdaDatabaseMonitoringRole"
  database_parameters  = var.database_parameters
}

################################################################################
# EKS Configuration
################################################################################

resource "aws_iam_policy" "worker_policy" {
  name        = "worker-policy"
  description = "Worker policy for the ALB Ingress"

  policy = file("../../terraform/templates/eks/iam-policy.json")
}

module "eks" {
  source        = "../../terraform/modules/eks"
  subnet_ids    = module.networking.private_subnet_ids
  vpc_id        = module.networking.vpc_id
  worker_sg_ids = [module.eks_worker_sg.security_group_id]
  policy_arn    = aws_iam_policy.worker_policy.arn
}

################################################################################
# Kafka Configuration
################################################################################

module "kafka" {
  source     = "../../terraform/modules/kafka"
  subnet_ids = module.networking.private_subnet_ids
  msk_sg_ids = [module.eks_worker_sg.security_group_id]
}


data "template_file" "users_connector_initializer" {
  template = file("../../terraform/templates/debezium/connector.json.tpl")
  vars     = {
    database_hostname  = module.eda_database.db_endpoint
    database_user      = var.eda_database_username
    database_password  = var.eda_database_password
    database_name      = var.users_database_name
    bootstrap_servers  = module.kafka.bootstrap_brokers
    history_topic      = var.users_history_topic
    table_include_list = join(",", var.users_table_include_list)
  }
}

data "template_file" "orders_connector_initializer" {
  template = file("../../terraform/templates/debezium/connector.json.tpl")
  vars     = {
    database_hostname  = module.eda_database.db_endpoint
    database_user      = var.eda_database_username
    database_password  = var.eda_database_password
    database_name      = var.orders_database_name
    bootstrap_servers  = module.kafka.bootstrap_brokers
    history_topic      = var.orders_history_topic
    table_include_list = join(",", var.orders_table_include_list)
  }
}

data "template_file" "warehouse_connector_initializer" {
  template = file("../../terraform/templates/debezium/connector.json.tpl")
  vars     = {
    database_hostname  = module.eda_database.db_endpoint
    database_user      = var.eda_database_username
    database_password  = var.eda_database_password
    database_name      = var.warehouse_database_name
    bootstrap_servers  = module.kafka.bootstrap_brokers
    history_topic      = var.warehouse_history_topic
    table_include_list = join(",", var.warehouse_table_include_list)
  }
}

data "template_file" "payment_connector_initializer" {
  template = file("../../terraform/templates/debezium/connector.json.tpl")
  vars     = {
    database_hostname  = module.eda_database.db_endpoint
    database_user      = var.eda_database_username
    database_password  = var.eda_database_password
    database_name      = var.payments_database_name
    bootstrap_servers  = module.kafka.bootstrap_brokers
    history_topic      = var.payments_history_topic
    table_include_list = join(",", var.payments_table_include_list)
  }
}
