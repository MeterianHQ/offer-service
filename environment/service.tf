# ---------------------------------------------------------------------------------------------------------------------
# CREATE THE OFFER SERVICE AND AN ALB FOR IT
# ---------------------------------------------------------------------------------------------------------------------

variable rds_offers_engine_db_url {}
variable rds_offers_engine_db_user {}
variable rds_offers_engine_db_password {}

module "offer_service" {
  source = "./ecs-service"

  region = "${var.region}"
  ecs_cluster_id = "${module.ecs_cluster.ecs_cluster_id}"
  cpu = 1024
  memory = 800
  desired_count = 2

  offer_service_name = "${var.offer_service_name}"
  offer_service_image = "${lookup(var.offer_service_image, var.env)}"
  offer_service_version = "${var.offer_service_version}"
  offer_service_container_port = "${var.offer_service_port}"
  offer_service_host_port = "${var.offer_service_dynamic_mapping_host_port}"

  offer_mangement_ui_name = "${var.offer_management_ui_name}"
  offer_management_ui_image = "${lookup(var.offer_management_ui_image, var.env)}"
  offer_management_ui_version = "${var.offer_management_ui_version}"
  offer_management_ui_container_port = "${var.offer_managment_iu_port}"
  offer_management_ui_host_port = "${var.offer_service_dynamic_mapping_host_port}"

  alb_target_group_offer_management_ui_arn = "${module.offer_service_alb.alb_target_group_offer_management_ui_arn}"
  alb_target_group_offer_service_arn = "${module.offer_service_alb.alb_target_group_offer_service_arn}"
  alb_main_id = "${module.offer_service_alb.alb_main_id}"
  alb_offer_management_ui_port = "${module.offer_service_alb.alb_offer_management_ui_port}"
  alb_offer_service_port = "${module.offer_service_alb.alb_offer_service_port}"

  num_env_vars = 1
  env_vars = "${map(
    "EXTRA_JAVA_PARAMS", "-Dspring.datasource.url=${var.rds_offers_engine_db_url} -Dspring.datasource.username=${var.rds_offers_engine_db_user} -Dspring.datasource.password=${var.rds_offers_engine_db_password}"
  )}"
}

module "offer_service_alb" {
  source = "./alb"

  name = "${var.offer_service_name}"

  vpc_id = "${lookup(var.vpc_id, var.env)}"
  subnet_ids = ["${var.public_subnets}"]

  offer_service_port = "${var.offer_service_port}"
  offer_service_health_check_path = "/health"

  offer_managment_iu_port = "${var.offer_managment_iu_port}"
  offer_managment_iu_health_check_path = "/index.html"

  /*public_zone_id = "${lookup(var.public_zone_id, var.env)}"*/
  public_url = "offer-service.${var.env}.ovotech.org.uk"
}
