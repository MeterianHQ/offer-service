# ---------------------------------------------------------------------------------------------------------------------
# CREATE THE OFFER SERVICE AND AN ELB FOR IT
# ---------------------------------------------------------------------------------------------------------------------

variable rds_offers_engine_db_url {}
variable rds_offers_engine_db_.username {}
variable rds_offers_engine_db_password {}

module "offer_service" {
  source = "./ecs-service"

  region = "${var.region}"
  name = "${var.offer_service_name}service"
  ecs_cluster_id = "${module.ecs_cluster.ecs_cluster_id}"

  image = "${lookup(var.offer_service_image, var.env)}"
  image_version = "${var.offer_service_version}"
  cpu = 1024
  memory = 800
  desired_count = 1

  container_port = "${var.offer_service_port}"
  host_port = "${var.offer_service_port}"
  elb_name = "${module.offer_service_elb.elb_name}"
    num_env_vars = 1
    env_vars = "${map(
      "EXTRA_JAVA_PARAMS", "-Dspring.datasource.url=${var.rds_offers_engine_db_url} -Dspring.datasource.username=${{var.rds_offers_engine_db_user}} -Dspring.datasource.password=${{var.rds_offers_engine_db_password}}"
    )}"
}

module "offer_service_elb" {
  source = "./elb"

  name = "${var.offer_service_name}service"

  vpc_id = "${lookup(var.vpc_id, var.env)}"
  subnet_ids = ["${var.public_subnets}"]

  instance_port = "${var.offer_service_port}"
  health_check_path = "health"

  /*public_zone_id = "${lookup(var.public_zone_id, var.env)}"*/
  public_url = "offer-service.${var.env}.ovotech.org.uk"
}
