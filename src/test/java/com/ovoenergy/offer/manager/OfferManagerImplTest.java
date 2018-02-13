package com.ovoenergy.offer.manager;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.google.common.collect.Lists;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.manager.impl.OfferManagerImpl;
import com.ovoenergy.offer.test.utils.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(PowerMockRunner.class)
public class OfferManagerImplTest {

    private static final Long TEST_OFFER_ID = 11L;

    private static final String TEST_OFFER_CODE = "test offer code";

    private static final String TEST_EMAIL = "test@email.com";

    private static final Date TEST_NOW_DATE = new Date();

    private static final Long TEST_NOW_MILLISECONDS = TEST_NOW_DATE.toInstant().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    private static final Long TEST_MAX_REDEMPTIONS_VALID = 11L;

    private static final Long ACTUAL_REDEMPTIONS_VALID = 1L;

    @Mock
    private JdbcHelper mockJdbcHelper;

    @Mock
    private OfferRepository mockOfferRepository;

    @Mock
    private OfferRedeemRepository mockOfferRedeemRepository;

    @Mock
    private MessageSource mockMsgSource;

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();

    @Fixture
    private OfferDTO fixtureOfferDTO;

    @Fixture
    private OfferDBEntity fixtureOfferDBEntity;

    @Fixture
    private OfferRedeemDBEntity fxOfferRedeemDBEntity;

    @InjectMocks
    private OfferManager unit = new OfferManagerImpl();

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
        verify(mockOfferRepository).findOneById(eq(TEST_OFFER_ID));
    }

    @Test
    public void testGetAllOffersSuccess() {
        when(mockOfferRepository.findAll(any(Sort.class))).thenReturn(Lists.newArrayList(fixtureOfferDBEntity));

        List<OfferDTO> result = unit.getAllOffers();

        assertTrue(1 == result.size());
        verify(mockOfferRepository).findAll(any(Sort.class));
    }

    @Test
    public void testCreateOfferSuccess() {
        fixtureOfferDTO.setOfferCode(TEST_OFFER_CODE);
        when(mockOfferRepository.findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE))).thenReturn(null);
        when(mockJdbcHelper.lookupCurrentDbTime()).thenReturn(TEST_NOW_DATE);
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
        verify(mockJdbcHelper).lookupCurrentDbTime();
        verify(mockOfferRepository).save(any(OfferDBEntity.class));
        verify(mockOfferRepository).findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE));
    }

    @Test
    public void verifyOfferSuccess() {
        fixtureOfferDBEntity.setStartDate(TEST_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setExpiryDate(TEST_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setMaxOfferRedemptions(TEST_MAX_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setActualOfferRedemptions(ACTUAL_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setIsExpirable(false);
        when(mockOfferRepository.findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE))).thenReturn(fixtureOfferDBEntity);
        when(mockJdbcHelper.lookupCurrentDbTime()).thenReturn(TEST_NOW_DATE);

        Boolean result = unit.verifyOffer(TEST_OFFER_CODE);

        assertTrue(result);
        verify(mockOfferRepository).findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE));
        verify(mockJdbcHelper).lookupCurrentDbTime();
    }

    @Test
    public void ApplyOfferSuccess() {
        OfferApplyDTO offerApplyDTO = new OfferApplyDTO();
        offerApplyDTO.setEmail(TEST_EMAIL);
        offerApplyDTO.setOfferCode(TEST_OFFER_CODE);

        fixtureOfferDBEntity.setStartDate(TEST_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setExpiryDate(TEST_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setMaxOfferRedemptions(TEST_MAX_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setActualOfferRedemptions(ACTUAL_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setIsExpirable(false);
        when(mockOfferRepository.findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE))).thenReturn(fixtureOfferDBEntity);
        when(mockJdbcHelper.lookupCurrentDbTime()).thenReturn(TEST_NOW_DATE);
        when(mockOfferRedeemRepository.save(any(OfferRedeemDBEntity.class))).thenReturn(fxOfferRedeemDBEntity);
        when(mockOfferRepository.save(any(OfferDBEntity.class))).thenReturn(fixtureOfferDBEntity);

        OfferApplyDTO result = unit.applyUserToOffer(TEST_OFFER_CODE, TEST_EMAIL);

        assertEquals(fxOfferRedeemDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_OFFER_CODE, result.getOfferCode());
        verify(mockOfferRepository).findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE));
        verify(mockJdbcHelper, times(2)).lookupCurrentDbTime();
        verify(mockOfferRepository).save(any(OfferDBEntity.class));
        verify(mockOfferRedeemRepository).save(any(OfferRedeemDBEntity.class));
    }
}