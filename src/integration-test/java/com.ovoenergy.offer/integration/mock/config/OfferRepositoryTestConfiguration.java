package com.ovoenergy.offer.integration.mock.config;

import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class OfferRepositoryTestConfiguration {

    @MockBean(name = "offerRepository")
    public OfferRepository offerRepository;

    @MockBean(name = "offerRedeemRepository")
    public OfferRedeemRepository offerRedeemRepository;

    @MockBean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate;
}
