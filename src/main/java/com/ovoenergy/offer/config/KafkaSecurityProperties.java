package com.ovoenergy.offer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka.security")
public class KafkaSecurityProperties {

    private String protocol;
    private String truststoreLocation;
    private String truststorePassword;
    private String keystoreType;
    private String keystoreLocation;
    private String keystorePassword;
    private String keyPassword;
}
