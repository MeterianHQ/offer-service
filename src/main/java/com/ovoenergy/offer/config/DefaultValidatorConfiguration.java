package com.ovoenergy.offer.config;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class DefaultValidatorConfiguration {

    @Bean(autowire = Autowire.BY_NAME)
    public Validator defaultValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

}
