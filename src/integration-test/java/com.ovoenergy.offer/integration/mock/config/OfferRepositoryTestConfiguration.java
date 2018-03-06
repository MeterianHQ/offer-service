package com.ovoenergy.offer.integration.mock.config;

import com.ovoenergy.offer.Application;
import com.ovoenergy.offer.audit.EntityListenersConfiguration;
import com.ovoenergy.offer.config.KafkaConfiguration;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.kafka.TestKafkaListeners;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@ComponentScan(basePackages = "com.ovoenergy.offer", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {Application.class, EntityListenersConfiguration.class,
                KafkaConfiguration.class, TestKafkaListeners.class
        })
})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, SpringDataWebAutoConfiguration.class,
        KafkaAutoConfiguration.class})
@Configuration
public class OfferRepositoryTestConfiguration {

    @MockBean(name = "offerRepository")
    public OfferRepository offerRepository;

    @MockBean(name = "offerRedeemRepository")
    public OfferRedeemRepository offerRedeemRepository;

    @MockBean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate;
}
