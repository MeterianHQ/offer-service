output "offer_service_url" {
  value = "http://${module.offer_service_alb.alb_dns_name}"
}