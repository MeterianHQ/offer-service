package com.ovoenergy.offer.manager.operation;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OfferOperationsRegistryTest {

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private OfferOperationsRegistry offerOperationsRegistry;
    @Mock
    private ActiveOfferStrategy activeOfferStrategy;
    @Mock
    private DraftOfferStrategy draftOfferStrategy;

    @Fixture
    private OfferDTO fixtureOfferDTO;
    @Fixture
    private OfferDBEntity fixtureOfferDBEntity;

    @Before
    public void setUp() {
        offerOperationsRegistry.init();
    }

    @Test
    public void testCreateOfferDBEntityInvalidStatus() {
        fixtureOfferDTO.setStatus("unknown");

        expectedException.expect(instanceOf(VariableNotValidException.class));

        offerOperationsRegistry.createOfferDBEntity(fixtureOfferDTO);
    }

    @Test
    public void testCreateOfferDBEntity() {
        fixtureOfferDTO.setStatus("active");
        when(activeOfferStrategy.createOfferDBEntity(eq(fixtureOfferDTO))).thenReturn(new OfferDBEntity());

        OfferDBEntity offerDBEntity = offerOperationsRegistry.createOfferDBEntity(fixtureOfferDTO);

        assertThat(offerDBEntity, is(notNullValue()));

        verify(activeOfferStrategy, only()).createOfferDBEntity(eq(fixtureOfferDTO));
        verifyNoMoreInteractions(activeOfferStrategy);
        verifyZeroInteractions(draftOfferStrategy);
    }

    @Test
    public void testUpdateOfferDBEntity() {
        fixtureOfferDTO.setStatus("draft");
        when(draftOfferStrategy.updateOfferDBEntity(eq(fixtureOfferDBEntity), eq(fixtureOfferDTO))).thenReturn(new OfferDBEntity());

        OfferDBEntity offerDBEntity = offerOperationsRegistry.updateOfferDBEntity(fixtureOfferDBEntity, fixtureOfferDTO);

        assertThat(offerDBEntity, is(notNullValue()));

        verify(draftOfferStrategy, only()).updateOfferDBEntity(eq(fixtureOfferDBEntity), eq(fixtureOfferDTO));
        verifyNoMoreInteractions(draftOfferStrategy);
        verifyZeroInteractions(activeOfferStrategy);
    }

    @Test
    public void testProcessOfferDBEntityValidation() {
        fixtureOfferDBEntity.setStatus(StatusType.ACTIVE);
        when(activeOfferStrategy.processOfferDBEntityValidation(eq(fixtureOfferDBEntity))).thenReturn(new OfferDBEntity());

        OfferDBEntity offerDBEntity = offerOperationsRegistry.processOfferDBEntityValidation(fixtureOfferDBEntity);

        assertThat(offerDBEntity, is(notNullValue()));

        verify(activeOfferStrategy, only()).processOfferDBEntityValidation(eq(fixtureOfferDBEntity));
        verifyNoMoreInteractions(activeOfferStrategy);
        verifyZeroInteractions(draftOfferStrategy);
    }

    @Test
    public void testCreateOfferRedeemDBEntity() {
        fixtureOfferDBEntity.setStatus(StatusType.DRAFT);
        when(draftOfferStrategy.createOfferRedeemDBEntity(any(OfferDBEntity.class), anyString())).thenReturn(new OfferRedeemDBEntity());

        OfferRedeemDBEntity offerRedeemDBEntity = offerOperationsRegistry.createOfferRedeemDBEntity(fixtureOfferDBEntity, "email");

        assertThat(offerRedeemDBEntity, is(notNullValue()));

        verify(draftOfferStrategy, only()).createOfferRedeemDBEntity(eq(fixtureOfferDBEntity), anyString());
        verifyNoMoreInteractions(draftOfferStrategy);
        verifyZeroInteractions(activeOfferStrategy);
    }
}