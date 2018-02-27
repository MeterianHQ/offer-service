# ---------------------------------------------------------------------------------------------------------------------
# CREATE AN ALB
# ---------------------------------------------------------------------------------------------------------------------
resource "aws_alb" "main" {
  name = "${var.name}"
  internal = false
  subnets = ["${var.subnet_ids}"]
  security_groups = ["${aws_security_group.alb.id}"]
}

resource "aws_alb_target_group" "offer_service" {
  name     = "offer-service-alb-tgroup"
  port     = "${var.offer_service_port}"
  protocol = "HTTP"
  vpc_id = "${var.vpc_id}"
  health_check {
    healthy_threshold = 2
    unhealthy_threshold = 2
    timeout = 5
    interval = 15
    path = "${var.offer_service_health_check_path}"
  }

}

resource "aws_alb_target_group" "offer_management_ui" {
  name     = "offer-management-ui-alb-tgroup"
  port     = "${var.offer_managment_iu_port}"
  protocol = "HTTP"
  vpc_id = "${var.vpc_id}"
  health_check {
    healthy_threshold = 2
    unhealthy_threshold = 2
    timeout = 5
    interval = 15
    path = "${var.offer_managment_iu_health_check_path}"
  }
}

# ---------------------------------------------------------------------------------------------------------------------
# CREATE A SECURITY GROUP THAT CONTROLS WHAT TRAFFIC CAN GO IN AND OUT OF THE ALB
# ---------------------------------------------------------------------------------------------------------------------

resource "aws_security_group" "alb" {
  name = "${var.name}"
  description = "The security group for the ${var.name} ALB"
  vpc_id = "${var.vpc_id}"
}

resource "aws_security_group_rule" "all_outbound_all" {
  type = "egress"
  from_port = 0
  to_port = 0
  protocol = "-1"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = "${aws_security_group.alb.id}"
}

resource "aws_security_group_rule" "all_offer_mnagement_inbound_all" {
  type = "ingress"
  from_port = "${var.lb_offer_management_ui_port}"
  to_port = "${var.lb_offer_management_ui_port}"
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = "${aws_security_group.alb.id}"
}

resource "aws_security_group_rule" "all_offer_service_inbound_all" {
  type = "ingress"
  from_port = "${var.lb_offer_service_port}"
  to_port = "${var.lb_offer_service_port}"
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = "${aws_security_group.alb.id}"
}
