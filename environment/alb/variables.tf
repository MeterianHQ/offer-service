# ---------------------------------------------------------------------------------------------------------------------
# REQUIRED MODULE PARAMETERS
# These variables must be passed in by the operator.
# ---------------------------------------------------------------------------------------------------------------------

variable "name" {
  description = "The name of the ALB"
}

variable "vpc_id" {
  description = "The ID of the VPC in which to deploy the ALB."
}

variable "subnet_ids" {
  description = "The subnet IDs in which to deploy the ALB."
  type = "list"
}

variable "offer_service_port" {
  description = "The port the offer_service application on EC2 Instance is listening on. The ALB will route traffic to this port."
}

variable "offer_managment_iu_port" {
  description = "The port the offer_service application on EC2 Instance is listening on. The ALB will route traffic to this port."
}

variable "offer_managment_iu_health_check_path" {
  description = "The path on the instance the ALB can use for health checks for offer_management_ui. Do NOT include a leading slash."
}

variable "offer_service_health_check_path" {
  description = "The path on the instance the ALB can use for health checks for offer_service. Do NOT include a leading slash."
}

/*variable "public_zone_id" {
  description = "The ID of the public hosted zone."
}*/

variable "public_url" {
  description = "The public URL with which someone can access the load balancer."
}

variable "lb_offer_service_port" {
  description = "The port the ALB listens requests for offers_service."
  default = 443
}

variable "lb_offer_management_ui_port" {
  description = "The port the ALB listens on requests for offers_management_ui."
  default = 80
}