terraform {
  required_version = "> 0.9.3"
  backend "s3" {
    # bucket should be defined during `terraform init -backend-config "bucket=<bucket name>"`
    key = "terraform.tfstate"
    region = "eu-west-1"
  }
}

provider "aws" {
  version = "~> 1.8.0"
  region = "${var.region}"
  profile = "uat"
}

provider "template" {
  version = "~> 0.1"
}