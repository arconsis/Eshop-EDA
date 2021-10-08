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
  source               = "../modules/network"
  create_vpc           = var.create_vpc
  create_igw           = var.create_igw
  single_nat_gateway   = var.single_nat_gateway
  enable_nat_gateway   = var.enable_nat_gateway
  region               = var.aws_region
  vpc_name             = var.vpc_name
  cidr_block           = var.cidr_block
  availability_zones   = var.availability_zones
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
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
# Database Configuration
################################################################################
# Orders Database
module "orders_database" {
  source = "terraform-aws-modules/rds/aws"

  identifier = "orders-database"

  engine                      = "postgres"
  engine_version              = "11.12"
  auto_minor_version_upgrade  = false
  family                      = "postgres11" # DB parameter group
  major_engine_version        = "11"         # DB option group
  instance_class              = "db.t3.small"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_encrypted     = false

  # NOTE: Do NOT use 'user' as the value for 'username' as it throws:
  # "Error creating DB Instance: InvalidParameterValue: MasterUsername
  # user cannot be used as it is a reserved word used by the engine"
  name     = "postgres"
  username = var.orders_database_username
  password = var.orders_database_password
  port     = 5432

  multi_az               = true
  subnet_ids             = module.networking.private_subnet_ids
  vpc_security_group_ids = [module.private_vpc_sg.security_group_id]

  maintenance_window              = "Mon:00:00-Mon:03:00"
  backup_window                   = "03:00-06:00"
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false

  performance_insights_enabled          = true
  performance_insights_retention_period = 7
  create_monitoring_role                = true
  monitoring_interval                   = 60

  parameters = [
    {
      name  = "autovacuum"
      value = 1
    },
    {
      name  = "client_encoding"
      value = "utf8"
    }
  ]

  db_option_group_tags = {
    "Sensitive" = "low"
  }
  db_parameter_group_tags = {
    "Sensitive" = "low"
  }
  db_subnet_group_tags = {
    "Sensitive" = "high"
  }
}
# Payments Database
module "payments_database" {
  source = "terraform-aws-modules/rds/aws"

  identifier = "payments-database"

  engine                      = "postgres"
  engine_version              = "11.12"
  auto_minor_version_upgrade  = false
  family                      = "postgres11" # DB parameter group
  major_engine_version        = "11"         # DB option group
  instance_class              = "db.t3.small"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_encrypted     = false

  name     = "postgres"
  username = var.payments_database_username
  password = var.payments_database_password
  port     = 5432

  multi_az               = true
  subnet_ids             = module.networking.private_subnet_ids
  vpc_security_group_ids = [module.private_vpc_sg.security_group_id]

  maintenance_window              = "Mon:00:00-Mon:03:00"
  backup_window                   = "03:00-06:00"
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false

  performance_insights_enabled          = true
  performance_insights_retention_period = 7
  create_monitoring_role                = true
  monitoring_interval                   = 60

  parameters = [
    {
      name  = "autovacuum"
      value = 1
    },
    {
      name  = "client_encoding"
      value = "utf8"
    }
  ]

  db_option_group_tags = {
    "Sensitive" = "low"
  }
  db_parameter_group_tags = {
    "Sensitive" = "low"
  }
  db_subnet_group_tags = {
    "Sensitive" = "high"
  }
}
# Shipments Database
module "shipments_database" {
  source = "terraform-aws-modules/rds/aws"

  identifier = "shipments-database"

  engine                      = "postgres"
  engine_version              = "11.12"
  auto_minor_version_upgrade  = false
  family                      = "postgres11" # DB parameter group
  major_engine_version        = "11"         # DB option group
  instance_class              = "db.t3.small"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_encrypted     = false

  name     = "postgres"
  username = var.shipments_database_username
  password = var.shipments_database_password
  port     = 5432

  multi_az               = true
  subnet_ids             = module.networking.private_subnet_ids
  vpc_security_group_ids = [module.private_vpc_sg.security_group_id]

  maintenance_window              = "Mon:00:00-Mon:03:00"
  backup_window                   = "03:00-06:00"
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false

  performance_insights_enabled          = true
  performance_insights_retention_period = 7
  create_monitoring_role                = true
  monitoring_interval                   = 60

  parameters = [
    {
      name  = "autovacuum"
      value = 1
    },
    {
      name  = "client_encoding"
      value = "utf8"
    }
  ]

  db_option_group_tags = {
    "Sensitive" = "low"
  }
  db_parameter_group_tags = {
    "Sensitive" = "low"
  }
  db_subnet_group_tags = {
    "Sensitive" = "high"
  }
}
# Warehouse Database
module "warehouse_database" {
  source = "terraform-aws-modules/rds/aws"

  identifier = "warehouse-database"

  engine                      = "postgres"
  engine_version              = "11.12"
  auto_minor_version_upgrade  = false
  family                      = "postgres11" # DB parameter group
  major_engine_version        = "11"         # DB option group
  instance_class              = "db.t3.small"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_encrypted     = false

  name     = "postgres"
  username = var.warehouse_database_username
  password = var.warehouse_database_password
  port     = 5432

  multi_az               = true
  subnet_ids             = module.networking.private_subnet_ids
  vpc_security_group_ids = [module.private_vpc_sg.security_group_id]

  maintenance_window              = "Mon:00:00-Mon:03:00"
  backup_window                   = "03:00-06:00"
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false

  performance_insights_enabled          = true
  performance_insights_retention_period = 7
  create_monitoring_role                = true
  monitoring_interval                   = 60

  parameters = [
    {
      name  = "autovacuum"
      value = 1
    },
    {
      name  = "client_encoding"
      value = "utf8"
    }
  ]

  db_option_group_tags = {
    "Sensitive" = "low"
  }
  db_parameter_group_tags = {
    "Sensitive" = "low"
  }
  db_subnet_group_tags = {
    "Sensitive" = "high"
  }
}
# Users Database
module "users_database" {
  source = "terraform-aws-modules/rds/aws"

  identifier = "users-database"

  engine                      = "postgres"
  engine_version              = "11.12"
  auto_minor_version_upgrade  = false
  family                      = "postgres11" # DB parameter group
  major_engine_version        = "11"         # DB option group
  instance_class              = "db.t3.small"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_encrypted     = false

  name     = "postgres"
  username = var.users_database_username
  password = var.users_database_password
  port     = 5432

  multi_az               = true
  subnet_ids             = module.networking.private_subnet_ids
  vpc_security_group_ids = [module.private_vpc_sg.security_group_id]

  maintenance_window              = "Mon:00:00-Mon:03:00"
  backup_window                   = "03:00-06:00"
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false

  performance_insights_enabled          = true
  performance_insights_retention_period = 7
  create_monitoring_role                = true
  monitoring_interval                   = 60

  parameters = [
    {
      name  = "autovacuum"
      value = 1
    },
    {
      name  = "client_encoding"
      value = "utf8"
    }
  ]

  db_option_group_tags = {
    "Sensitive" = "low"
  }
  db_parameter_group_tags = {
    "Sensitive" = "low"
  }
  db_subnet_group_tags = {
    "Sensitive" = "high"
  }
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
      asg_max_size         = 5
    }
  ]

  write_kubeconfig       = true
  kubeconfig_output_path = "./"

  workers_additional_policies = [aws_iam_policy.worker_policy.arn]
  cluster_enabled_log_types = [
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
