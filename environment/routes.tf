/*resource "aws_route53_record" "offer-service-cname" {
  zone_id = "${lookup(var.public_zone_id, var.env)}"
  name = "${var.service_url}"
  type = "CNAME"
  ttl = "300"
  records = ["${module.offer_service_elb.elb_dns_name}"]
}*/
