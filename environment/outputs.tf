output "offer_service_url" {
  value = "http://${module.offer_service_elb.elb_dns_name}"
}