package com.ovoenergy.offer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static com.google.common.collect.Lists.newArrayList;

@EnableSwagger2
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.ovoenergy.offer")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        LOGGER.info("Starting offer service with arguments: {}", newArrayList(args));
        SpringApplication.run(Application.class, args).start();
    }
}
