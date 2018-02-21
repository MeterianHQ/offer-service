terraform {
  required_version = "> 0.9.3"
  #backend "s3" {
    # bucket should be defined during `terraform init -backend-config "bucket=<bucket name>"`
    # key = "terraform.tfstate"
    # region = "eu-west-1"
  #}
  backend "local" {
    path = "status/terraform.tfstate"
  }
}

provider "aws" {
  version = "~> 1.8.0"
  region = "${var.region}"
  shared_credentials_file = "~/.aws/credentials"
  profile = "uat"
}

provider "template" {
  version = "~> 0.1"
}