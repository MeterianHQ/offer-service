package com.ovoenergy.offer.integration.mock.config;

import com.ovoenergy.offer.db.repository.OfferRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("integrationtest")
public class OfferRepositoryTestConfiguration {

    @MockBean(name="offerRepository")
    public OfferRepository offerRepository;
}