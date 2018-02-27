# ---------------------------------------------------------------------------------------------------------------------
# REQUIRED MODULE PARAMETERS
# These variables must be passed in by the operator.
# ---------------------------------------------------------------------------------------------------------------------

variable "name" {
  description = "The name of the ECS Cluster."
}

variable "min_size" {
  description = "The minimum number of EC2 Instances to run in the autoscaling group."
}

variable "max_size" {
  description = "The maximum number of EC2 Instances to run in the autoscaling group."
}

variable "desired_capacity" {
  description = "The desired number of EC2 Instances to run in the autoscaling group."
}

variable "instance_type" {
  description = "The type of EC2 Instance to deploy in the ECS Cluster (e.g. t2.micro)."
}

variable "vpc_id" {
  description = "The ID of the VPC in which to deploy the ECS Cluster."
}

variable "subnet_ids" {
  description = "The subnet IDs in which to deploy the EC2 Instances of the ECS Cluster."
  type = "list"
}

# ---------------------------------------------------------------------------------------------------------------------
# OPTIONAL MODULE PARAMETERS
# These variables have defaults, but may be overridden by the operator.
# ---------------------------------------------------------------------------------------------------------------------

variable "allow_inbound_ports_and_cidr_blocks" {
  description = "A map of port to CIDR block. For each entry in this map, the ESC Cluster will allow incoming requests on the specified port from the specified CIDR blocks."
  type = "map"
  default = {}
}

variable "key_pair_name" {
  description = "The name of an EC2 Key Pair to associate with each EC2 Instance in the ECS Cluster. Leave blank to not associate a Key Pair."
}

variable "ami_id" {
  description = "The AMI to be used for the EC2 instances (must be ECS optimised)."
  default = "ami-1d46df64"
}

variable "allow_ssh_from_cidr_blocks" {
  description = "The list of CIDR-formatted IP address ranges from which the EC2 Instances in the ECS Cluster should accept SSH connections."
  type = "list"
  default = []
}