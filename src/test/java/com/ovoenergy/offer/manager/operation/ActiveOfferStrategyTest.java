package com.ovoenergy.offer.manager.operation;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.dto.OfferDTO;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActiveOfferStrategyTest {

    @InjectMocks
    private ActiveOfferStrategy activeOfferStrategy;
    @Mock
    private JdbcHelper jdbcHelper;

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();

    @Fixture
    private OfferDTO fixtureOfferDTO;

    @Fixture
    private OfferDBEntity fixtureOfferDBEntity;

    @Test
    public void testCreateOfferDBEntity() {
        Date now = new Date();
        when(jdbcHelper.lookupCurrentDbTime()).thenReturn(now);

        OfferDBEntity offerDBEntity = activeOfferStrategy.createOfferDBEntity(fixtureOfferDTO);

        assertThat(offerDBEntity.getStatus(), is(StatusType.ACTIVE));
        assertThat(offerDBEntity.getId(), is(nullValue()));
        assertThat(offerDBEntity.getActualOfferRedemptions(), is(0L));
        assertThat(offerDBEntity.getUpdatedOn(), is(now.getTime()));

        verify(jdbcHelper, only()).lookupCurrentDbTime();
        verifyNoMoreInteractions(jdbcHelper);
    }

    @Test
    public void testUpdateOfferDBEntity() {
        Date now = new Date();
        when(jdbcHelper.lookupCurrentDbTime()).thenReturn(now);
        fixtureOfferDBEntity.setStatus(StatusType.ACTIVE);

        OfferDBEntity offerDBEntity = activeOfferStrategy.updateOfferDBEntity(fixtureOfferDBEntity, fixtureOfferDTO);

        assertThat(offerDBEntity.getStatus(), is(StatusType.ACTIVE));
        assertThat(offerDBEntity.getId(), is(fixtureOfferDBEntity.getId()));
        assertThat(offerDBEntity.getActualOfferRedemptions(), is(fixtureOfferDBEntity.getActualOfferRedemptions()));
        assertThat(offerDBEntity.getStartDate(), is(fixtureOfferDBEntity.getStartDate()));
        assertThat(offerDBEntity.getUpdatedOn(), is(now.getTime()));

        verify(jdbcHelper, only()).lookupCurrentDbTime();
        verifyNoMoreInteractions(jdbcHelper);
    }
}