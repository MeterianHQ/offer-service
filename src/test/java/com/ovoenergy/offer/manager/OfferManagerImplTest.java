package com.ovoenergy.offer.manager;

import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ovoenergy.offer.config.RedemptionLinkProperties;
import com.ovoenergy.offer.db.entity.*;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferLinkGenerateDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.manager.impl.HashGeneratorImpl;
import com.ovoenergy.offer.manager.impl.OfferManagerImpl;
import com.ovoenergy.offer.manager.operation.OfferOperationsRegistry;
import com.ovoenergy.offer.manager.redirect.GetVoucherRedirectHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private OfferRepository mockOfferRepository;

    @Mock
    private OfferRedeemRepository mockOfferRedeemRepository;

    @Mock
    private OfferOperationsRegistry mockOfferOperationsRegistry;

    @Spy
    private HashGenerator hashGenerator = new HashGeneratorImpl();

    @Mock
    private JdbcHelper jdbcHelper;

    @Fixture
    private OfferDTO fixtureOfferDTO;

    @Fixture
    private OfferDBEntity fixtureOfferDBEntity;

    @Fixture
    private OfferRedeemDBEntity fxOfferRedeemDBEntity;

    @InjectMocks
    private OfferManagerImpl unit;

    @Fixture
    private OfferLinkGenerateDTO fixtureOfferLinkGenerateDTO;

    @Mock
    private RedemptionLinkProperties redemptionLinkProperties;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private GetVoucherRedirectHandler mockGetVoucherRedirectHandler;

    @Test
    public void testGetOfferByIdSuccess() {
        when(mockOfferRepository.findOne(eq(TEST_OFFER_ID))).thenReturn(fixtureOfferDBEntity);

        OfferDTO result = unit.getOfferById(TEST_OFFER_ID);

        assertEquals(fixtureOfferDBEntity.getOfferCode(), result.getOfferCode());
        assertEquals(fixtureOfferDBEntity.getDescription(), result.getDescription());
        assertEquals(fixtureOfferDBEntity.getOfferName(), result.getOfferName());
        assertEquals(fixtureOfferDBEntity.getChannel().value(), result.getChannel());
        assertEquals(fixtureOfferDBEntity.getActualOfferRedemptions(), result.getActualOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getMaxOfferRedemptions().toString(), result.getMaxOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getExpiryDate(), result.getExpiryDate());
        assertEquals(fixtureOfferDBEntity.getStartDate(), result.getStartDate());
        assertEquals(fixtureOfferDBEntity.getIsExpirable(), result.getIsExpirable());
        assertEquals(fixtureOfferDBEntity.getEligibilityCriteria().value(), result.getEligibilityCriteria());
        assertEquals(fixtureOfferDBEntity.getOfferType().value(), result.getOfferType());
        assertEquals(fixtureOfferDBEntity.getStatus().name(), result.getStatus());
        assertEquals(fixtureOfferDBEntity.getSupplier().value(), result.getSupplier());
        assertEquals(fixtureOfferDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(fixtureOfferDBEntity.getValue().toString(), result.getValue());
        assertEquals(fixtureOfferDBEntity.getId(), result.getId());

        verify(mockOfferRepository, only()).findOne(eq(TEST_OFFER_ID));
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
        when(mockOfferRepository.findOne(anyLong())).thenReturn(fixtureOfferDBEntity);
        when(mockOfferOperationsRegistry.updateOfferDBEntity(fixtureOfferDBEntity, fixtureOfferDTO)).thenReturn(fixtureOfferDBEntity);
        when(mockOfferRepository.save(any(OfferDBEntity.class))).thenReturn(fixtureOfferDBEntity);

        OfferDTO result = unit.createOffer(fixtureOfferDTO);

        assertEquals(fixtureOfferDBEntity.getOfferCode(), result.getOfferCode());
        assertEquals(fixtureOfferDBEntity.getDescription(), result.getDescription());
        assertEquals(fixtureOfferDBEntity.getOfferName(), result.getOfferName());
        assertEquals(fixtureOfferDBEntity.getChannel().value(), result.getChannel());
        assertEquals(fixtureOfferDBEntity.getActualOfferRedemptions(), result.getActualOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getMaxOfferRedemptions().toString(), result.getMaxOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getExpiryDate(), result.getExpiryDate());
        assertEquals(fixtureOfferDBEntity.getStartDate(), result.getStartDate());
        assertEquals(fixtureOfferDBEntity.getIsExpirable(), result.getIsExpirable());
        assertEquals(fixtureOfferDBEntity.getEligibilityCriteria().value(), result.getEligibilityCriteria());
        assertEquals(fixtureOfferDBEntity.getOfferType().value(), result.getOfferType());
        assertEquals(fixtureOfferDBEntity.getStatus().name(), result.getStatus());
        assertEquals(fixtureOfferDBEntity.getSupplier().value(), result.getSupplier());
        assertEquals(fixtureOfferDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(fixtureOfferDBEntity.getValue().toString(), result.getValue());
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
        assertEquals(fixtureOfferDBEntity.getMaxOfferRedemptions().toString(), result.getMaxOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getExpiryDate(), result.getExpiryDate());
        assertEquals(fixtureOfferDBEntity.getStartDate(), result.getStartDate());
        assertEquals(fixtureOfferDBEntity.getIsExpirable(), result.getIsExpirable());
        assertEquals(fixtureOfferDBEntity.getEligibilityCriteria().value(), result.getEligibilityCriteria());
        assertEquals(fixtureOfferDBEntity.getOfferType().value(), result.getOfferType());
        assertEquals(fixtureOfferDBEntity.getStatus().name(), result.getStatus());
        assertEquals(fixtureOfferDBEntity.getSupplier().value(), result.getSupplier());
        assertEquals(fixtureOfferDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(fixtureOfferDBEntity.getValue().toString(), result.getValue());
        assertEquals(fixtureOfferDBEntity.getId(), result.getId());

        verify(mockOfferRepository, times(1)).save(any(OfferDBEntity.class));
        verify(mockOfferRepository, times(1)).findOne(anyLong());
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
        when(mockOfferRepository.findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE))).thenReturn(fixtureOfferDBEntity);
        when(mockOfferOperationsRegistry.processOfferDBEntityValidation(eq(fixtureOfferDBEntity))).thenReturn(fixtureOfferDBEntity);

        Boolean result = unit.verifyOffer(TEST_OFFER_CODE);

        assertTrue(result);
        verify(mockOfferRepository, only()).findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE));
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

        when(mockOfferRedeemRepository.findByEmailAndOfferDBEntityOfferCodeIgnoreCase(anyString(), anyString())).thenReturn(null);
        when(mockOfferOperationsRegistry.createOfferRedeemDBEntity(eq(fixtureOfferDBEntity), eq(TEST_EMAIL))).thenReturn(fxOfferRedeemDBEntity);
        when(mockOfferOperationsRegistry.processOfferDBEntityValidation(eq(fixtureOfferDBEntity))).thenReturn(fixtureOfferDBEntity);
        when(mockOfferRepository.findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE))).thenReturn(fixtureOfferDBEntity);
        when(mockOfferRedeemRepository.save(any(OfferRedeemDBEntity.class))).thenReturn(fxOfferRedeemDBEntity);
        when(mockOfferRepository.save(any(OfferDBEntity.class))).thenReturn(fixtureOfferDBEntity);

        OfferApplyDTO result = unit.applyUserToOffer(TEST_OFFER_CODE, TEST_EMAIL);

        assertEquals(fxOfferRedeemDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_OFFER_CODE, result.getOfferCode());
        verify(mockOfferRepository, times(1)).findOneByOfferCodeIgnoreCase(eq(TEST_OFFER_CODE));
        verify(mockOfferRepository, times(1)).save(any(OfferDBEntity.class));
        verify(mockOfferRedeemRepository, times(1)).save(any(OfferRedeemDBEntity.class));
        verify(mockOfferOperationsRegistry, times(1)).processOfferDBEntityValidation(eq(fixtureOfferDBEntity));
        verify(mockOfferOperationsRegistry, times(1)).createOfferRedeemDBEntity(eq(fixtureOfferDBEntity), eq(TEST_EMAIL));
        verify(mockOfferRedeemRepository, times(1)).findByEmailAndOfferDBEntityOfferCodeIgnoreCase(anyString(), anyString());
        verifyNoMoreInteractions(mockOfferRepository, mockOfferOperationsRegistry, mockOfferRedeemRepository);
    }

    @Test
    public void applyOfferSuccessExists() {
        OfferApplyDTO offerApplyDTO = new OfferApplyDTO();
        offerApplyDTO.setEmail(TEST_EMAIL);
        offerApplyDTO.setOfferCode(TEST_OFFER_CODE);

        fixtureOfferDBEntity.setStartDate(TEST_DAY_BEFORE_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setExpiryDate(TEST_NOW_MILLISECONDS);
        fixtureOfferDBEntity.setMaxOfferRedemptions(TEST_MAX_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setActualOfferRedemptions(ACTUAL_REDEMPTIONS_VALID);
        fixtureOfferDBEntity.setIsExpirable(false);
        fixtureOfferDBEntity.setStatus(StatusType.ACTIVE);

        when(mockOfferRedeemRepository.findByEmailAndOfferDBEntityOfferCodeIgnoreCase(anyString(), anyString())).thenReturn(fxOfferRedeemDBEntity);

        OfferApplyDTO result = unit.applyUserToOffer(TEST_OFFER_CODE, TEST_EMAIL);

        assertEquals(fxOfferRedeemDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_OFFER_CODE, result.getOfferCode());
        verify(mockOfferRedeemRepository, only()).findByEmailAndOfferDBEntityOfferCodeIgnoreCase(anyString(), anyString());
        verifyNoMoreInteractions(mockOfferRedeemRepository);
        verifyZeroInteractions(mockOfferRepository, mockOfferOperationsRegistry);
    }

    @Test
    public void testGenerateOfferLink() {
        fxOfferRedeemDBEntity.setStatus(OfferRedeemStatusType.CREATED);

        when(redemptionLinkProperties.getMilliseconds()).thenReturn(1_000L);
        when(mockOfferRedeemRepository.findByEmailAndOfferDBEntityId(anyString(), anyLong())).thenReturn(fxOfferRedeemDBEntity);
        when(mockOfferRedeemRepository.saveAndFlush(any(OfferRedeemDBEntity.class))).thenReturn(fxOfferRedeemDBEntity);
        when(jdbcHelper.lookupCurrentDbTime()).thenReturn(new Date());

        String offerLink = unit.generateOfferLink(fixtureOfferLinkGenerateDTO);

        assertThat(offerLink, containsString(fixtureOfferLinkGenerateDTO.getOfferId().toString()));
        assertThat(offerLink, containsString(fixtureOfferLinkGenerateDTO.getEmail()));

        verify(mockOfferRedeemRepository, times(1)).findByEmailAndOfferDBEntityId(eq(fixtureOfferLinkGenerateDTO.getEmail()), eq(fixtureOfferLinkGenerateDTO.getOfferId()));
        verify(hashGenerator, only()).generateHash(eq(fxOfferRedeemDBEntity));
        verify(mockOfferRedeemRepository, times(1)).saveAndFlush(any(OfferRedeemDBEntity.class));
        verify(jdbcHelper, only()).lookupCurrentDbTime();
        verify(redemptionLinkProperties, only()).getMilliseconds();
        verifyNoMoreInteractions(mockOfferRedeemRepository, hashGenerator, mockOfferRedeemRepository, jdbcHelper, redemptionLinkProperties);
    }

    @Test
    public void testGenerateOfferLinkExists() {
        fxOfferRedeemDBEntity.setStatus(OfferRedeemStatusType.GENERATED);
        when(mockOfferRedeemRepository.findByEmailAndOfferDBEntityId(anyString(), anyLong())).thenReturn(fxOfferRedeemDBEntity);

        String offerLink = unit.generateOfferLink(fixtureOfferLinkGenerateDTO);

        assertThat(offerLink, containsString(fixtureOfferLinkGenerateDTO.getOfferId().toString()));
        assertThat(offerLink, containsString(fixtureOfferLinkGenerateDTO.getEmail()));

        verify(mockOfferRedeemRepository, only()).findByEmailAndOfferDBEntityId(anyString(), anyLong());
        verifyNoMoreInteractions(mockOfferRedeemRepository, hashGenerator, mockOfferRedeemRepository, jdbcHelper);
    }

    @Test
    public void testGetOfferRedeemInfoRedirectToNotFound() {
        when(mockOfferRedeemRepository.findByEmailAndOfferDBEntityIdAndHash(anyString(), anyLong(), anyString())).thenReturn(null);
        PowerMockito.doNothing().when(mockGetVoucherRedirectHandler).processNotFoundVoucherRedirect(any());

        unit.processRedemptionLinkRedirect("hash", "email@email.com", 1L, mockResponse);

        verify(mockOfferRedeemRepository, times(1)).findByEmailAndOfferDBEntityIdAndHash(anyString(), anyLong(), anyString());
        verifyNoMoreInteractions(mockOfferRedeemRepository, jdbcHelper);
        verify(mockGetVoucherRedirectHandler).processNotFoundVoucherRedirect(any());
    }

    @Test
    public void testGetOfferRedeemInfoRedirectToExpired() {
        when(mockOfferRedeemRepository.findByEmailAndOfferDBEntityIdAndHash(anyString(), anyLong(), anyString())).thenReturn(fxOfferRedeemDBEntity);
        when(jdbcHelper.lookupCurrentDbTime()).thenReturn(new Date(fxOfferRedeemDBEntity.getExpiredOn() + 1));
        PowerMockito.doNothing().when(mockGetVoucherRedirectHandler).processExpiredVoucherLinkRedirect(any(), any());

        unit.processRedemptionLinkRedirect("hash", "email@email.com", 1L, mockResponse);

        verify(mockOfferRedeemRepository, times(1)).findByEmailAndOfferDBEntityIdAndHash(anyString(), anyLong(), anyString());
        verify(jdbcHelper, times(1)).lookupCurrentDbTime();
        verifyNoMoreInteractions(mockOfferRedeemRepository, jdbcHelper, mockOfferRepository);
        verify(mockGetVoucherRedirectHandler).processExpiredVoucherLinkRedirect(any(), any());
    }

    @Test
    public void testGetOfferRedeemInfoRedirect() {
        when(mockOfferRedeemRepository.findByEmailAndOfferDBEntityIdAndHash(anyString(), anyLong(), anyString())).thenReturn(fxOfferRedeemDBEntity);
        when(jdbcHelper.lookupCurrentDbTime()).thenReturn(new Date(fxOfferRedeemDBEntity.getExpiredOn() - 1));
        when(mockOfferRedeemRepository.saveAndFlush(any(OfferRedeemDBEntity.class))).then(AdditionalAnswers.returnsFirstArg());
        when(mockOfferRepository.saveAndFlush(any(OfferDBEntity.class))).then(AdditionalAnswers.returnsFirstArg());
        PowerMockito.doNothing().when(mockGetVoucherRedirectHandler).processGetVoucherInfoRedirect(any(), any(), any());
        fxOfferRedeemDBEntity.setOfferRedeemEventDBEntities(Sets.newHashSet(new OfferRedeemEventDBEntity(1L, fxOfferRedeemDBEntity,  1L,OfferRedeemStatusType.GENERATED)));

        unit.processRedemptionLinkRedirect("hash", "email@email.com", 1L, mockResponse);

        verify(mockOfferRedeemRepository, times(1)).findByEmailAndOfferDBEntityIdAndHash(anyString(), anyLong(), anyString());
        verify(mockOfferRedeemRepository, times(1)).saveAndFlush(any(OfferRedeemDBEntity.class));
        verify(mockOfferRepository, times(1)).saveAndFlush(any(OfferDBEntity.class));
        verify(jdbcHelper, times(1)).lookupCurrentDbTime();
        verifyNoMoreInteractions(mockOfferRedeemRepository, jdbcHelper, mockOfferRepository);
        verify(mockGetVoucherRedirectHandler).processGetVoucherInfoRedirect(any(), any(), any());
    }
}
