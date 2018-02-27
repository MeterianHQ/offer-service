output "offer_service_task_arn" {
  value = "${aws_ecs_task_definition.offer_service_task.arn}"
}

output "offer_mangement_ui_task_arn" {
  value = "${aws_ecs_task_definition.offer_mangement_ui_task.arn}"
}

output "offer_service_id" {
  value = "${aws_ecs_service.offer_service.id}"
}

output "offer_mangement_ui_id" {
  value = "${aws_ecs_service.offer_mangement_ui.id}"
}

output "iam_offer_service_role_id" {
  value = "${aws_iam_role.ecs_offer_service_role.id}"
}

output "iam_offer_mangement_ui_id" {
  value = "${aws_iam_role.ecs_offer_mangement_ui_role.id}"
}