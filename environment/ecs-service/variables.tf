# ---------------------------------------------------------------------------------------------------------------------
# REQUIRED MODULE PARAMETERS
# These variables must be passed in by the operator.
# ---------------------------------------------------------------------------------------------------------------------

variable "region" {
  description = "The region of the ECS Service."
}

variable "offer_service_name" {
  description = "The name of the offer_service ECS Service."
}

variable "offer_mangement_ui_name" {
  description = "The name of the offer_mangement_ui_name ECS Service."
}

variable "ecs_cluster_id" {
  description = "The ID of the ECS Cluster this ECS Service should run in."
}

variable "offer_service_image" {
  description = "The Docker image to run in the ECS Task (e.g. foo/bar)."
}

variable "offer_management_ui_image" {
  description = "The Docker image to run in the ECS Task (e.g. foo/bar)."
}

variable "offer_service_version" {
  description = "The version of the Docker image to run in the ECS Task. This is the the tag on the Docker image (e.g. latest or v3)."
}

variable "offer_management_ui_version" {
  description = "The version of the Docker image to run in the ECS Task. This is the the tag on the Docker image (e.g. latest or v3)."
}

variable "cpu" {
  description = "The number of CPU units to give the ECS Task, where 1024 represents one vCPU."
}

variable "memory" {
  description = "The amount of memory, in MB, to give the ECS Task."
}

variable "offer_service_container_port" {
  description = "The port the Docker container in the ECS Task is listening on."
}

variable "offer_management_ui_container_port" {
  description = "The port the Docker container in the ECS Task is listening on."
}

variable "offer_service_host_port" {
  description = "The port on the host to map to var.container_port."
}

variable "offer_management_ui_host_port" {
  description = "The port on the host to map to var.container_port."
}

variable "desired_count" {
  description = "The number of ECS Tasks to run for this ECS Service."
}

variable "alb_target_group_offer_service_id" {
  description = "The id of the ALB offer_service target group with which this ECS Service should register."
}

variable "alb_target_group_offer_management_ui_id" {
  description = "The id of the ALB offer_management_ui target group with which this ECS Service should register."
}

variable "alb_main_id" {
  description = "The id of the ALB main."
}

variable "alb_offer_service_port" {
  description = "The offer_service port in ALB."
}

variable "alb_offer_management_ui_port" {
  description = "The offer_management_ui port in ALB."
}

# ---------------------------------------------------------------------------------------------------------------------
# OPTIONAL MODULE PARAMETERS
# These variables have defaults, but may be overridden by the operator.
# ---------------------------------------------------------------------------------------------------------------------

variable "env_vars" {
  description = "The environment variables to make available in each ECS Task. Any time you update this variable, make sure to update var.num_env_vars too!"
  type = "map"
  default = {}
}

variable "num_env_vars" {
  description = "The number of environment variables in var.env_vars. We should be able to compute this automatically, but can't due to a limitation where Terraform cannot compute count on dynamic data: https://github.com/hashicorp/terraform/issues/12570."
  default = 0
}

variable "deployment_maximum_percent" {
  description = "The upper limit, as a percentage of var.desired_count, of the number of running ECS Tasks that can be running in a service during a deployment. Setting this to more than 100 means that during deployment, ECS will deploy new instances of a Task before undeploying the old ones."
  default = 200
}

variable "deployment_minimum_healthy_percent" {
  description = "The lower limit, as a percentage of var.desired_count, of the number of running ECS Tasks that must remain running and healthy in a service during a deployment. Setting this to less than 100 means that during deployment, ECS may undeploy old instances of a Task before deploying new ones."
  default = 50
}