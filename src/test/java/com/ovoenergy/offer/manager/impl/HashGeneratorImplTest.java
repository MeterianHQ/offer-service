package com.ovoenergy.offer.manager.impl;

import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.manager.HashGenerator;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class HashGeneratorImplTest {

    private HashGenerator hashGenerator = new HashGeneratorImpl();

    private JFixture jFixture = new JFixture();

    {
        jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    }

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures(jFixture);

    @Fixture
    private OfferRedeemDBEntity fxOfferRedeemDBEntity;

    @Test
    public void testGenerateHash() {
        String hash = hashGenerator.generateHash(fxOfferRedeemDBEntity);

        assertThat(hash, is(notNullValue()));
        assertThat(hash.length(), is(64));
    }
}