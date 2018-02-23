output "alb_name" {
  value = "${aws_alb.main.name}"
}

output "alb_dns_name" {
  value = "${aws_alb.main.dns_name}"
}

output "alb_security_group_id" {
  value = "${aws_security_group.alb.id}"
}

output "alb_target_group_offer_service_id" {
 value = "${aws_alb_target_group.offer_service.id}"
}

output "alb_target_group_offer_management_ui_id" {
  value = "${aws_alb_target_group.offer_management_ui.id}}"
}

output "alb_main_id" {
  value = "${aws_alb.main.id}"
}

output "alb_offer_service_port" {
  value = "${var.lb_offer_service_port}"
}

output "alb_offer_management_ui_port" {
  value = "${var.lb_offer_management_ui_port}"
}