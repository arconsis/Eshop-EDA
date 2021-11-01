provider "aws" {
  shared_credentials_file = "$HOME/.aws/credentials"
  profile                 = "default"
  region                  = var.aws_region
}

data "aws_eks_cluster" "cluster" {
  name = module.eks.cluster_id
}

data "aws_eks_cluster_auth" "cluster" {
  name = module.eks.cluster_id
}

provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
  token                  = data.aws_eks_cluster_auth.cluster.token
}

################################################################################
# VPC Configuration
################################################################################
module "networking" {
  source                         = "../modules/network"
  create_vpc                     = var.create_vpc
  create_igw                     = var.create_igw
  single_nat_gateway             = var.single_nat_gateway
  enable_nat_gateway             = var.enable_nat_gateway
  region                         = var.aws_region
  vpc_name                       = var.vpc_name
  cidr_block                     = var.cidr_block
  availability_zones             = var.availability_zones
  public_subnet_cidrs            = var.public_subnet_cidrs
  private_subnet_cidrs           = var.private_subnet_cidrs
  public_subnet_additional_tags  = {
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
module "private_vpc_sg" {
  source                   = "../modules/security"
  create_vpc               = var.create_vpc
  create_sg                = true
  sg_name                  = "private-database-security-group"
  description              = "Controls access to the private database (not internet facing)"
  rule_ingress_description = "allow inbound access only from resources in VPC"
  rule_egress_description  = "allow all outbound"
  vpc_id                   = module.networking.vpc_id
  ingress_cidr_blocks      = [var.cidr_block]
  ingress_from_port        = 0
  ingress_to_port          = 0
  ingress_protocol         = "-1"
  egress_cidr_blocks       = ["0.0.0.0/0"]
  egress_from_port         = 0
  egress_to_port           = 0
  egress_protocol          = "-1"
}

################################################################################
# Database Configuration
################################################################################
# Orders Database
module "orders_database" {
  source                = "../modules/database"
  database_identifier   = "orders-database"
  database_username     = var.orders_database_username
  database_password     = var.orders_database_password
  subnet_ids            = module.networking.private_subnet_ids
  security_group_ids    = [module.private_vpc_sg.security_group_id]
  monitoring_role_name  = "OrdersDatabaseMonitoringRole"
}
# Payments Database
module "payments_database" {
  source                = "../modules/database"
  database_identifier   = "payments-database"
  database_username     = var.payments_database_username
  database_password     = var.payments_database_password
  subnet_ids            = module.networking.private_subnet_ids
  security_group_ids    = [module.private_vpc_sg.security_group_id]
  monitoring_role_name  = "PaymentsDatabaseMonitoringRole"
}
# Shipments Database
module "shipments_database" {
  source                = "../modules/database"
  database_identifier   = "shipments-database"
  database_username     = var.shipments_database_username
  database_password     = var.shipments_database_password
  subnet_ids            = module.networking.private_subnet_ids
  security_group_ids    = [module.private_vpc_sg.security_group_id]
  monitoring_role_name  = "ShipmentsDatabaseMonitoringRole"
}
# Warehouse Database
module "warehouse_database" {
  source                = "../modules/database"
  database_identifier   = "warehouse-database"
  database_username     = var.warehouse_database_username
  database_password     = var.warehouse_database_password
  subnet_ids            = module.networking.private_subnet_ids
  security_group_ids    = [module.private_vpc_sg.security_group_id]
  monitoring_role_name  = "WarehouseDatabaseMonitoringRole"
}
# Users Database
module "users_database" {
  source                = "../modules/database"
  database_identifier   = "users-database"
  database_username     = var.users_database_username
  database_password     = var.users_database_password
  subnet_ids            = module.networking.private_subnet_ids
  security_group_ids    = [module.private_vpc_sg.security_group_id]
  monitoring_role_name  = "UsersDatabaseMonitoringRole"
}

module "eks" {
  source          = "terraform-aws-modules/eks/aws"
  cluster_name    = var.cluster_name
  cluster_version = "1.21"

  subnets = module.networking.private_subnet_ids
  vpc_id  = module.networking.vpc_id

  # node_groups are aws eks managed nodes whereas worker_groups are self managed nodes. Among many one advantage of worker_groups is that you can use your custom AMI for the nodes.
  # https://github.com/terraform-aws-modules/terraform-aws-eks/issues/895
  worker_groups = [
    {
      name                 = "worker-group-1"
      instance_type        = "t2.small"
      asg_desired_capacity = 2
      asg_max_size         = 8
    }
  ]

  write_kubeconfig       = true
  kubeconfig_output_path = "./"

  workers_additional_policies = [aws_iam_policy.worker_policy.arn]
  cluster_enabled_log_types   = [
    "api",
    "audit",
    "authenticator",
    "controllerManager",
    "scheduler"
  ]
}

resource "aws_iam_policy" "worker_policy" {
  name        = "worker-policy"
  description = "Worker policy for the ALB Ingress"

  policy = file("../common/templates/eks/iam-policy.json")
}

provider "helm" {
  kubernetes {
    host                   = data.aws_eks_cluster.cluster.endpoint
    cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
    token                  = data.aws_eks_cluster_auth.cluster.token
  }
}

resource "helm_release" "ingress" {
  name       = "ingress"
  repository = "https://charts.bitnami.com/bitnami"
  chart      = "nginx-ingress-controller"

  create_namespace = true
  namespace        = "ingress-nginx"

  set {
    name  = "service.type"
    value = "LoadBalancer"
  }
  set {
    name  = "service.annotations"
    value = "service.beta.kubernetes.io/aws-load-balancer-type: nlb"
  }
}


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
    client_subnets  = module.networking.private_subnet_ids
    security_groups = [module.private_vpc_sg.security_group_id]
  }

  logging_info {
    broker_logs {
      cloudwatch_logs {
        enabled   = true
        log_group = aws_cloudwatch_log_group.msk_broker_logs.name
      }
    }
  }
}

output "zookeeper_connect_string" {
  value = aws_msk_cluster.kafka.zookeeper_connect_string
}

output "bootstrap_brokers_tls" {
  description = "TLS connection host:port pairs"
  value       = aws_msk_cluster.kafka.bootstrap_brokers_tls
}
