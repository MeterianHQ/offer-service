# ---------------------------------------------------------------------------------------------------------------------
# CREATE THE ECS CLUSTER
# ---------------------------------------------------------------------------------------------------------------------

module "ecs_cluster" {
  source = "./ecs-cluster"

  name = "${var.offer_service_name}cluster"
  min_size = 1
  max_size = 5
  desired_capacity = 1
  instance_type = "t2.micro"
  key_pair_name = "${var.key_pair_name}"

  vpc_id = "${lookup(var.vpc_id, var.env)}"
  subnet_ids = ["${var.private_subnets}"]

  # To keep the example simple to test, we allow SSH access from anywhere. In real-world use cases, you should lock
  # this down just to trusted IP addresses.
  allow_ssh_from_cidr_blocks = ["0.0.0.0/0"]

  # Here, we allow the EC2 Instances in the ECS Cluster to recieve requests on the ports used by the rails-frontend
  # and sinatra-backend. To keep the example simple to test, we allow these requests from any IP, but in real-world
  # use cases, you should lock this down to just the IP addresses of the ELB and other trusted parties.
  allow_inbound_ports_and_cidr_blocks = "${map(
    var.offer_service_port, "0.0.0.0/0"
  )}"
}

resource "aws_security_group_rule" "allow_rds_access" {
  type = "  ingress"
  from_port = 5432
  to_port = 5432
  protocol = "tcp"
  security_group_id = "${lookup(var.rds_sg, var.env)}"
  source_security_group_id = "${module.ecs_cluster.security_group_id}"
}
