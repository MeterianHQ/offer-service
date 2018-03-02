package com.ovoenergy.offer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mail.comms.link")
public class MailCommsLinkProperties extends AbstractMailCommsProperties {

}
