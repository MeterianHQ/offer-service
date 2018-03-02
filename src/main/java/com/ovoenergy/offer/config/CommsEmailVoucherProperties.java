package com.ovoenergy.offer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "comms.email.get.voucher.template")
public class CommsEmailVoucherProperties extends AbstractCommsProperties {

}
