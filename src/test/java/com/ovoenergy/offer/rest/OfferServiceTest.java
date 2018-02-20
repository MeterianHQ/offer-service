package com.ovoenergy.offer.rest;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.dto.OfferVerifyDTO;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.validation.CustomValidationProcessor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OfferServiceTest {

    @InjectMocks
    private OfferService unit = new OfferService();

    private static final String TEST_FIELD = "testField";

    private static final String TEST_OFFER_CODE = "testOfferCode";

    private static final String TEST_EMAIL = "test@email.com";

    @Mock
    private CustomValidationProcessor mockCustomValidator;

    @Mock
    private OfferManager mockOfferManager;

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();

    @Fixture
    private OfferDTO fixtureOfferDTO;

    @Test
    public void testCreateOfferSuccess() {
        when(mockCustomValidator.processActiveOfferInputDataValidationViolations(any())).thenReturn(null);
        when(mockOfferManager.createOffer(eq(fixtureOfferDTO))).thenReturn(fixtureOfferDTO);

        ResponseEntity<OfferDTO> response = unit.createOffer(fixtureOfferDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fixtureOfferDTO, fixtureOfferDTO);
        verify(mockCustomValidator, only()).processActiveOfferInputDataValidationViolations(fixtureOfferDTO);
        verify(mockOfferManager).createOffer(eq(fixtureOfferDTO));
    }

    @Test
    public void testCreateOfferValidationError() {
        OfferValidationDTO validationDTO = new OfferValidationDTO(fixtureOfferDTO);
        Map<String, Set<ErrorMessageDTO>> violations = new HashMap<>();
        violations.put(TEST_FIELD, Sets.newHashSet(new ErrorMessageDTO("ERR1", "ERR1")));
        validationDTO.setConstraintViolations(violations);
        when(mockCustomValidator.processActiveOfferInputDataValidationViolations(any())).thenReturn(validationDTO);

        ResponseEntity<OfferDTO> response = unit.createOffer(fixtureOfferDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(mockCustomValidator, only()).processActiveOfferInputDataValidationViolations(eq(fixtureOfferDTO));
        assertEquals(fixtureOfferDTO.getChannel(), response.getBody().getChannel());
        assertEquals(fixtureOfferDTO.getDescription(), response.getBody().getDescription());
        assertEquals(fixtureOfferDTO.getEligibilityCriteria(), response.getBody().getEligibilityCriteria());
        assertEquals(fixtureOfferDTO.getExpiryDate(), response.getBody().getExpiryDate());
        assertEquals(fixtureOfferDTO.getMaxOfferRedemptions(), response.getBody().getMaxOfferRedemptions());
        assertEquals(fixtureOfferDTO.getIsExpirable(), response.getBody().getIsExpirable());
        assertEquals(fixtureOfferDTO.getOfferCode(), response.getBody().getOfferCode());
        assertEquals(fixtureOfferDTO.getOfferName(), response.getBody().getOfferName());
        assertEquals(fixtureOfferDTO.getOfferType(), response.getBody().getOfferType());
        assertEquals(fixtureOfferDTO.getSupplier(), response.getBody().getSupplier());
        assertEquals(fixtureOfferDTO.getStartDate(), response.getBody().getStartDate());
        assertEquals(1, ((OfferValidationDTO) response.getBody()).getConstraintViolations().get(TEST_FIELD).size());
    }

    @Test
    public void testGetAllOffersSuccess() {
        when(mockOfferManager.getAllOffers()).thenReturn(Lists.newArrayList(fixtureOfferDTO));

        ResponseEntity<List<OfferDTO>> result = unit.getAllOffers();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(mockOfferManager).getAllOffers();
    }

    @Test
    public void testVerifyOfferSuccess() {
        OfferVerifyDTO offerVerifyDTO = new OfferVerifyDTO();
        offerVerifyDTO.setOfferCode(TEST_OFFER_CODE);

        when(mockOfferManager.verifyOffer(eq(offerVerifyDTO.getOfferCode()))).thenReturn(true);

        ResponseEntity<Boolean> result = unit.verifyOffer(offerVerifyDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody());
        verify(mockOfferManager).verifyOffer(eq(offerVerifyDTO.getOfferCode()));
    }

    @Test
    public void testApplyOfferSuccess() {
        OfferApplyDTO offerApplyDTO = new OfferApplyDTO();
        offerApplyDTO.setOfferCode(TEST_OFFER_CODE);
        offerApplyDTO.setEmail(TEST_EMAIL);

        when(mockOfferManager.applyUserToOffer(eq(offerApplyDTO.getOfferCode()), eq(offerApplyDTO.getEmail()))).thenReturn(offerApplyDTO);

        ResponseEntity<OfferApplyDTO> result = unit.applyToOfferOffer(offerApplyDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(TEST_EMAIL, result.getBody().getEmail());
        assertEquals(TEST_OFFER_CODE, result.getBody().getOfferCode());
        verify(mockOfferManager).applyUserToOffer(eq(offerApplyDTO.getOfferCode()), eq(offerApplyDTO.getEmail()));
    }

}