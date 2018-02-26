package com.ovoenergy.offer.manager;

import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.google.common.collect.Lists;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.manager.impl.OfferManagerImpl;
import com.ovoenergy.offer.manager.operation.OfferOperationsRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OfferManagerImplTest {

    private static final Long TEST_OFFER_ID = 11L;

    private static final String TEST_OFFER_CODE = "test offer code";

    private static final String TEST_EMAIL = "test@email.com";

    private static final Date TEST_NOW_DATE = new Date();

    private static final Long TEST_NOW_MILLISECONDS = TEST_NOW_DATE.toInstant().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    private static final Long TEST_DAY_BEFORE_NOW_MILLISECONDS = TEST_NOW_DATE.toInstant().atZone(ZoneId.of("UTC")).minusDays(1).toInstant().toEpochMilli();

    private static final Long TEST_MAX_REDEMPTIONS_VALID = 11L;

    private static final Long ACTUAL_REDEMPTIONS_VALID = 1L;

    private JFixture jFixture = new JFixture();

    {
        jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    }

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures(jFixture);

    @Mock
    private OfferRepository mockOfferRepository;

    @Mock
    private OfferRedeemRepository mockOfferRedeemRepository;

    @Mock
    private OfferOperationsRegistry mockOfferOperationsRegistry;

    @Fixture
    private OfferDTO fixtureOfferDTO;

    @Fixture
    private OfferDBEntity fixtureOfferDBEntity;

    @Fixture
    private OfferRedeemDBEntity fxOfferRedeemDBEntity;

    @InjectMocks
    private OfferManagerImpl unit;

    @Test
    public void testGetOfferByIdSuccess() {
        when(mockOfferRepository.findOneById(eq(TEST_OFFER_ID))).thenReturn(fixtureOfferDBEntity);

        OfferDTO result = unit.getOfferById(TEST_OFFER_ID);

        assertEquals(fixtureOfferDBEntity.getOfferCode(), result.getOfferCode());
        assertEquals(fixtureOfferDBEntity.getDescription(), result.getDescription());
        assertEquals(fixtureOfferDBEntity.getOfferName(), result.getOfferName());
        assertEquals(fixtureOfferDBEntity.getChannel().value(), result.getChannel());
        assertEquals(fixtureOfferDBEntity.getActualOfferRedemptions(), result.getActualOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getMaxOfferRedemptions(), result.getMaxOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getExpiryDate(), result.getExpiryDate());
        assertEquals(fixtureOfferDBEntity.getStartDate(), result.getStartDate());
        assertEquals(fixtureOfferDBEntity.getIsExpirable(), result.getIsExpirable());
        assertEquals(fixtureOfferDBEntity.getEligibilityCriteria().value(), result.getEligibilityCriteria());
        assertEquals(fixtureOfferDBEntity.getOfferType().value(), result.getOfferType());
        assertEquals(fixtureOfferDBEntity.getStatus().name(), result.getStatus());
        assertEquals(fixtureOfferDBEntity.getSupplier().value(), result.getSupplier());
        assertEquals(fixtureOfferDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(fixtureOfferDBEntity.getValue(), result.getValue());
        assertEquals(fixtureOfferDBEntity.getId(), result.getId());

        verify(mockOfferRepository, only()).findOneById(eq(TEST_OFFER_ID));
        verifyNoMoreInteractions(mockOfferRepository);
        verifyZeroInteractions(mockOfferRedeemRepository, mockOfferOperationsRegistry);
    }

    @Test
    public void testGetAllOffersSuccess() {
        when(mockOfferRepository.findAll(any(Sort.class))).thenReturn(Lists.newArrayList(fixtureOfferDBEntity));

        List<OfferDTO> result = unit.getAllOffers();

        assertEquals(1, result.size());

        verify(mockOfferRepository, only()).findAll(any(Sort.class));
        verifyNoMoreInteractions(mockOfferRepository);
        verifyZeroInteractions(mockOfferRedeemRepository, mockOfferOperationsRegistry);
    }

    @Test
    public void testCreateOfferSuccess() {
        fixtureOfferDTO.setOfferCode(TEST_OFFER_CODE);
        fixtureOfferDTO.setStatus(StatusType.ACTIVE.name());
        when(mockOfferRepository.findOneById(anyLong())).thenReturn(fixtureOfferDBEntity);
        when(mockOfferOperationsRegistry.updateOfferDBEntity(fixtureOfferDBEntity, fixtureOfferDTO)).thenReturn(fixtureOfferDBEntity);
        when(mockOfferRepository.save(any(OfferDBEntity.class))).thenReturn(fixtureOfferDBEntity);

        OfferDTO result = unit.createOffer(fixtureOfferDTO);

        assertEquals(fixtureOfferDBEntity.getOfferCode(), result.getOfferCode());
        assertEquals(fixtureOfferDBEntity.getDescription(), result.getDescription());
        assertEquals(fixtureOfferDBEntity.getOfferName(), result.getOfferName());
        assertEquals(fixtureOfferDBEntity.getChannel().value(), result.getChannel());
        assertEquals(fixtureOfferDBEntity.getActualOfferRedemptions(), result.getActualOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getMaxOfferRedemptions(), result.getMaxOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getExpiryDate(), result.getExpiryDate());
        assertEquals(fixtureOfferDBEntity.getStartDate(), result.getStartDate());
        assertEquals(fixtureOfferDBEntity.getIsExpirable(), result.getIsExpirable());
        assertEquals(fixtureOfferDBEntity.getEligibilityCriteria().value(), result.getEligibilityCriteria());
        assertEquals(fixtureOfferDBEntity.getOfferType().value(), result.getOfferType());
        assertEquals(fixtureOfferDBEntity.getStatus().name(), result.getStatus());
        assertEquals(fixtureOfferDBEntity.getSupplier().value(), result.getSupplier());
        assertEquals(fixtureOfferDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(fixtureOfferDBEntity.getValue(), result.getValue());
        assertEquals(fixtureOfferDBEntity.getId(), result.getId());

        verify(mockOfferRepository, only()).save(any(OfferDBEntity.class));
        verify(mockOfferOperationsRegistry, only()).createOfferDBEntity(fixtureOfferDTO);
        verifyNoMoreInteractions(mockOfferRepository, mockOfferOperationsRegistry);
        verifyZeroInteractions(mockOfferRedeemRepository);
    }

    @Test
    public void testUpdateOfferSuccess() {
        Long id = 1L;
        fixtureOfferDTO.setOfferCode(TEST_OFFER_CODE);
        fixtureOfferDTO.setStatus(StatusType.ACTIVE.name());
        when(mockOfferOperationsRegistry.updateOfferDBEntity(fixtureOfferDBEntity, fixtureOfferDTO)).thenReturn(fixtureOfferDBEntity);
        when(mockOfferRepository.save(any(OfferDBEntity.class))).thenReturn(fixtureOfferDBEntity);

        OfferDTO result = unit.updateOffer(fixtureOfferDTO, id);

        assertEquals(fixtureOfferDBEntity.getOfferCode(), result.getOfferCode());
        assertEquals(fixtureOfferDBEntity.getDescription(), result.getDescription());
        assertEquals(fixtureOfferDBEntity.getOfferName(), result.getOfferName());
        assertEquals(fixtureOfferDBEntity.getChannel().value(), result.getChannel());
        assertEquals(fixtureOfferDBEntity.getActualOfferRedemptions(), result.getActualOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getMaxOfferRedemptions(), result.getMaxOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getExpiryDate(), result.getExpiryDate());
        assertEquals(fixtureOfferDBEntity.getStartDate(), result.getStartDate());
        assertEquals(fixtureOfferDBEntity.getIsExpirable(), result.getIsExpirable());
        assertEquals(fixtureOfferDBEntity.getEligibilityCriteria().value(), result.getEligibilityCriteria());
        assertEquals(fixtureOfferDBEntity.getOfferType().value(), result.getOfferType());
        assertEquals(fixtureOfferDBEntity.getStatus().name(), result.getStatus());
        assertEquals(fixtureOfferDBEntity.getSupplier().value(), result.getSupplier());
        assertEquals(fixtureOfferDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(fixtureOfferDBEntity.getValue(), result.getValue());
        assertEquals(fixtureOfferDBEntity.getId(), result.getId());

        verify(mockOfferRepository, times(1)).save(any(OfferDBEntity.class));
        verify(mockOfferRepository, times(1)).findOneById(anyLong());
        verify(mockOfferOperationsRegistry, only()).updateOfferDBEntity(any(OfferDBEntity.class), any(OfferDTO.class));
        verifyNoMoreInteractions(mockOfferRepository, mockOfferOperationsRegistry);
        verifyZeroInteractions(mockOfferRedeemRepository);
    }

    @Test
    public void verifyOfferSuccess() {
        fixtureOfferDBEntity.setStartDate(TEST_DAY_BEFORE_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setExpiryDate(TEST_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setMaxOfferRedemptions(TEST_MAX_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setActualOfferRedemptions(ACTUAL_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setIsExpirable(false);
        fixtureOfferDBEntity.setStatus(StatusType.ACTIVE);
        when(mockOfferRepository.findOneByOfferCodeIgnoreCaseAndStatus(eq(TEST_OFFER_CODE), eq(StatusType.ACTIVE))).thenReturn(fixtureOfferDBEntity);
        when(mockOfferOperationsRegistry.processOfferDBEntityValidation(eq(fixtureOfferDBEntity))).thenReturn(fixtureOfferDBEntity);

        Boolean result = unit.verifyOffer(TEST_OFFER_CODE);

        assertTrue(result);
        verify(mockOfferRepository, only()).findOneByOfferCodeIgnoreCaseAndStatus(eq(TEST_OFFER_CODE), eq(StatusType.ACTIVE));
        verify(mockOfferOperationsRegistry, only()).processOfferDBEntityValidation(eq(fixtureOfferDBEntity));
        verifyNoMoreInteractions(mockOfferRepository, mockOfferOperationsRegistry);
        verifyZeroInteractions(mockOfferRedeemRepository);
    }

    @Test(expected = VariableNotValidException.class)
    public void verifyOfferThrowException() {
        when(mockOfferRepository.findOneByOfferCodeIgnoreCaseAndStatus(eq(TEST_OFFER_CODE), eq(StatusType.ACTIVE))).thenReturn(null);

        unit.verifyOffer(TEST_OFFER_CODE);

        verify(mockOfferRepository, only()).findOneByOfferCodeIgnoreCaseAndStatus(eq(TEST_OFFER_CODE), eq(StatusType.ACTIVE));
        verifyNoMoreInteractions(mockOfferRepository);
        verifyZeroInteractions(mockOfferRedeemRepository, mockOfferOperationsRegistry);
    }

    @Test
    public void applyOfferSuccess() {
        OfferApplyDTO offerApplyDTO = new OfferApplyDTO();
        offerApplyDTO.setEmail(TEST_EMAIL);
        offerApplyDTO.setOfferCode(TEST_OFFER_CODE);

        fixtureOfferDBEntity.setStartDate(TEST_DAY_BEFORE_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setExpiryDate(TEST_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setMaxOfferRedemptions(TEST_MAX_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setActualOfferRedemptions(ACTUAL_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setIsExpirable(false);
        fixtureOfferDBEntity.setStatus(StatusType.ACTIVE);

        when(mockOfferOperationsRegistry.createOfferRedeemDBEntity(eq(fixtureOfferDBEntity), eq(TEST_EMAIL))).thenReturn(fxOfferRedeemDBEntity);
        when(mockOfferOperationsRegistry.processOfferDBEntityValidation(eq(fixtureOfferDBEntity))).thenReturn(fixtureOfferDBEntity);
        when(mockOfferRepository.findOneByOfferCodeIgnoreCaseAndStatus(eq(TEST_OFFER_CODE), eq(StatusType.ACTIVE))).thenReturn(fixtureOfferDBEntity);
        when(mockOfferRedeemRepository.save(any(OfferRedeemDBEntity.class))).thenReturn(fxOfferRedeemDBEntity);
        when(mockOfferRepository.save(any(OfferDBEntity.class))).thenReturn(fixtureOfferDBEntity);

        OfferApplyDTO result = unit.applyUserToOffer(TEST_OFFER_CODE, TEST_EMAIL);

        assertEquals(fxOfferRedeemDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_OFFER_CODE, result.getOfferCode());
        verify(mockOfferRepository, times(1)).findOneByOfferCodeIgnoreCaseAndStatus(eq(TEST_OFFER_CODE), eq(StatusType.ACTIVE));
        verify(mockOfferRepository, times(1)).save(any(OfferDBEntity.class));
        verify(mockOfferRedeemRepository, only()).save(any(OfferRedeemDBEntity.class));
        verify(mockOfferOperationsRegistry, times(1)).processOfferDBEntityValidation(eq(fixtureOfferDBEntity));
        verify(mockOfferOperationsRegistry, times(1)).createOfferRedeemDBEntity(eq(fixtureOfferDBEntity), eq(TEST_EMAIL));
        verifyNoMoreInteractions(mockOfferRepository, mockOfferOperationsRegistry, mockOfferRedeemRepository);
    }
}
