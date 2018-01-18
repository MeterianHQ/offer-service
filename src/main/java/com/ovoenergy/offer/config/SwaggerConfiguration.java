package com.ovoenergy.offer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

    @Value("${swagger.serviceVersion}")
    private String serviceVersion;

    @Value("${swagger.service.api.title}")
    private String swaggerServiceApiTitle;

    @Value("${swagger.service.api.description}")
    private String swaggerServiceApiDescription;

    @Value("${swagger.service.base.package}")
    private String swaggerServiceBasePackage;

    @Value("${swagger.service.api.contact.mail}")
    private String swaggerServiceApiContactMail;

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerServiceBasePackage))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                swaggerServiceApiTitle,
                swaggerServiceApiDescription,
                serviceVersion,
                null,
                swaggerServiceApiContactMail,
                null,
                null
        );
    }

}
