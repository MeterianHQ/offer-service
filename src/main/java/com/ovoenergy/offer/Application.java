package com.ovoenergy.offer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static com.google.common.collect.Lists.newArrayList;

@EnableSwagger2
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.ovoenergy.offer")
@EntityScan(basePackages = { "com.ovoenergy.offer.db.entity" })
@EnableJpaRepositories(basePackages = { "com.ovoenergy.offer.db.repository" })
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        LOGGER.info("Starting offer service with arguments: {}", newArrayList(args));
        SpringApplication.run(Application.class, args).start();
    }
}
