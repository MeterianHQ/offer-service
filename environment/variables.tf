# ---------------------------------------------------------------------------------------------------------------------
# ENVIRONMENT VARIABLES
# Define these secrets as environment variables
# ---------------------------------------------------------------------------------------------------------------------

# AWS_ACCESS_KEY_ID
# AWS_SECRET_ACCESS_KEY

variable "env" {
  description = "The environment Terraform should use (acceptable values is uat)."
}

variable "region" {
  description = "The region where to deploy this code."
  default = "eu-west-1"
}

variable "key_pair_name" {
  description = "The name of an EC2 Key Pair to associate with each EC2 Instance in the ECS Cluster. Leave blank to not associate a Key Pair."
  default = ""
}

variable "offer_service_name" {
  default = "offer-service"
  description = "The name of offer service."
}

variable "offer_service_image" {
  description = "The name of the Docker image to deploy for the offer service."
  type = "map"
  default = {
    uat  = "293486771097.dkr.ecr.eu-west-1.amazonaws.com/offer-service"
  }
}

variable "offer_service_version" {
  description = "The version (i.e. tag) of the Docker container to deploy for the offer service."
  default = "latest"
}

variable "offer_service_port" {
  description = "The port the offer service Docker container listens on for HTTP requests."
  default = 8080
}

variable "offer_management_ui_name" {
  default = "offer-management-ui"
  description = "The name of offer management ui"
}

variable "offer_management_ui_image" {
  description = "The name of the Docker image to deploy for the offer management ui."
  type = "map"
  default = {
    uat  = "293486771097.dkr.ecr.eu-west-1.amazonaws.com/offer-management-ui"
  }
}

variable "offer_management_ui_version" {
  description = "The version (i.e. tag) of the Docker container to deploy for the offer management ui"
  default = "latest"
}

variable "offer_managment_iu_port" {
  description = "The port the offer management ui Docker container listens on for HTTP requests."
  default = 4200
}

/*variable "public_zone_id" {
  description = "The ID of the public hosted zone."
  type = "map"
  default = {
    uat  = "Z3T5E3QIA5N4H4"
  }
}*/

variable "vpc_id" {
  description = "The VPC to be used"
  type = "map"
  default = {
    uat  = "vpc-ea69a38c"
  }
}

variable "private_subnets" {
  description = "Private subnets"
  type = "list"
}

variable "public_subnets" {
  description = "Public subnets"
  type = "list"
}

variable "rds_sg" {
  description = "The security group of the RDS instance."
  type = "map"
  default = {
    uat  = "sg-958430ef"
  }
}

variable "service_url" {
  description = "URL from which the offer-service is accessible"
}