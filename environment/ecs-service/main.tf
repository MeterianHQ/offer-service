# ---------------------------------------------------------------------------------------------------------------------
# CREATE AN ECS SERVICES TO RUN A LONG-RUNNING ECS TASKS
# We also associate the ECS Service with an ALB, which can distribute traffic across the ECS Tasks.
# ---------------------------------------------------------------------------------------------------------------------

resource "aws_alb_listener" "offer_management_ui" {
  load_balancer_arn = "${var.alb_main_id}"
  port              = "${var.alb_offer_management_ui_port}"
  protocol          = "HTTP"

  default_action {
    target_group_arn = "${var.alb_target_group_offer_management_ui_arn}"
    type             = "forward"
  }
}

resource "aws_alb_listener" "offer_service" {
  load_balancer_arn = "${var.alb_main_id}"
  port              = "${var.alb_offer_service_port}"
  protocol          = "HTTP"

  default_action {
    target_group_arn = "${var.alb_target_group_offer_service_arn}"
    type             = "forward"
  }
}

resource "aws_ecs_service" "offer_service" {
  name = "${var.offer_service_name}"
  cluster = "${var.ecs_cluster_id}"
  task_definition = "${aws_ecs_task_definition.offer_service_task.arn}"
  desired_count = "${var.desired_count}"
  iam_role = "${aws_iam_role.ecs_offer_service_role.arn}"

  deployment_minimum_healthy_percent = "${var.deployment_minimum_healthy_percent}"
  deployment_maximum_percent = "${var.deployment_maximum_percent}"

  load_balancer {
    target_group_arn = "${var.alb_target_group_offer_service_arn}"
    container_name   = "${var.offer_service_name}"
    container_port   = "${var.offer_service_container_port}"
  }

  depends_on = [
    "aws_iam_role_policy.ecs_offer_service_policy",
    "aws_alb_listener.offer_service"
  ]
}

resource "aws_ecs_service" "offer_mangement_ui" {
  name = "${var.offer_mangement_ui_name}"
  cluster = "${var.ecs_cluster_id}"
  task_definition = "${aws_ecs_task_definition.offer_mangement_ui_task.arn}"
  desired_count = "${var.desired_count}"
  iam_role = "${aws_iam_role.ecs_offer_mangement_ui_role.arn}"

  deployment_minimum_healthy_percent = "${var.deployment_minimum_healthy_percent}"
  deployment_maximum_percent = "${var.deployment_maximum_percent}"

  load_balancer {
    target_group_arn = "${var.alb_target_group_offer_management_ui_arn}"
    container_name   = "${var.offer_mangement_ui_name}"
    container_port   = "${var.offer_management_ui_container_port}"
  }

  depends_on = [
    "aws_iam_role_policy.ecs_offer_mangement_ui_policy",
    "aws_alb_listener.offer_management_ui"
  ]
}

# ---------------------------------------------------------------------------------------------------------------------
# CREATE A CLOUDWATCH LOG GROUP
# ---------------------------------------------------------------------------------------------------------------------

resource "aws_cloudwatch_log_group" "offer_service_container_log_group" {
  name = "${var.offer_service_name}"
}

resource "aws_cloudwatch_log_group" "offer_mangement_ui_container_log_group" {
  name = "${var.offer_mangement_ui_name}"
}

# ---------------------------------------------------------------------------------------------------------------------
# CREATE AN ECS TASK TO RUN A DOCKER CONTAINER for offer-service
# ---------------------------------------------------------------------------------------------------------------------

resource "aws_ecs_task_definition" "offer_service_task" {
  family = "${var.offer_service_name}"
  container_definitions = <<EOF
[
  {
    "name": "${var.offer_service_name}",
    "image": "${var.offer_service_image}:${var.offer_service_version}",
    "cpu": ${var.cpu},
    "memory": ${var.memory},
    "essential": true,
    "portMappings": [
      {
        "containerPort": ${var.offer_service_container_port},
        "hostPort": ${var.offer_service_host_port},
        "protocol": "tcp"
      }
    ],
    "environment": [${join(",", data.template_file.env_vars.*.rendered)}],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "${var.offer_service_name}",
        "awslogs-region": "${var.region}"
      }
    }
  }
]
EOF
}

resource "aws_ecs_task_definition" "offer_mangement_ui_task" {
  family = "${var.offer_mangement_ui_name}"
  container_definitions = <<EOF
[
  {
    "name": "${var.offer_mangement_ui_name}",
    "image": "${var.offer_management_ui_image}:${var.offer_management_ui_version}",
    "cpu": ${var.cpu},
    "memory": ${var.memory},
    "essential": true,
    "portMappings": [
      {
        "containerPort": ${var.offer_management_ui_container_port},
        "hostPort": ${var.offer_management_ui_host_port},
        "protocol": "tcp"
      }
    ],
    "environment": [${join(",", data.template_file.env_vars.*.rendered)}],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "${var.offer_mangement_ui_name}",
        "awslogs-region": "${var.region}"
      }
    }
  }
]
EOF
}

# Convert the environment variables the user passed-in into the format expected for for an ECS Task:
#
# "environment": [
#    {"name": "NAME", "value": "VALUE"},
#    {"name": "NAME", "value": "VALUE"},
#    ...
# ]
#
data "template_file" "env_vars" {
  count = "${var.num_env_vars}"
  template = <<EOF
{"name": "${element(keys(var.env_vars), count.index)}", "value": "${lookup(var.env_vars, element(keys(var.env_vars), count.index))}"}
EOF
}

# ---------------------------------------------------------------------------------------------------------------------
# CREATE AN IAM ROLE FOR THE ECS SERVICE
# ---------------------------------------------------------------------------------------------------------------------

resource "aws_iam_role" "ecs_offer_service_role" {
  name = "${var.offer_service_name}"
  assume_role_policy = "${data.aws_iam_policy_document.ecs_service_role.json}"
}

resource "aws_iam_role" "ecs_offer_mangement_ui_role" {
  name = "${var.offer_mangement_ui_name}"
  assume_role_policy = "${data.aws_iam_policy_document.ecs_service_role.json}"
}


data "aws_iam_policy_document" "ecs_service_role" {
  statement {
    effect = "Allow"
    actions = ["sts:AssumeRole"]
    principals {
      type = "Service"
      identifiers = ["ecs.amazonaws.com"]
    }
  }
}

# ---------------------------------------------------------------------------------------------------------------------
# ATTACH IAM PERMISSIONS TO THE IAM ROLE
# This IAM Policy allows the ECS Service to communicate with EC2 Instances.
# ---------------------------------------------------------------------------------------------------------------------

resource "aws_iam_role_policy" "ecs_offer_service_policy" {
  name = "ecs-ecs_offer_service_policy"
  role = "${aws_iam_role.ecs_offer_service_role.id}"
  policy = "${data.aws_iam_policy_document.ecs_service_policy.json}"
}

resource "aws_iam_role_policy" "ecs_offer_mangement_ui_policy" {
  name = "ecs-ecs_offer_mangement_ui_policy"
  role = "${aws_iam_role.ecs_offer_mangement_ui_role.id}"
  policy = "${data.aws_iam_policy_document.ecs_service_policy.json}"
}

data "aws_iam_policy_document" "ecs_service_policy" {
  statement {
    effect = "Allow"
    resources = ["*"]
    actions = [
      "elasticloadbalancing:Describe*",
      "elasticloadbalancing:DeregisterInstancesFromLoadBalancer",
      "elasticloadbalancing:RegisterInstancesWithLoadBalancer",
      "ec2:Describe*",
      "ec2:AuthorizeSecurityGroupIngress"
    ]
  }
}
