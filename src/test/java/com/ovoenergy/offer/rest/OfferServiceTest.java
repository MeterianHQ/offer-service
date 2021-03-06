package com.ovoenergy.offer.rest;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OfferServiceTest {

    private static final String TEST_FIELD = "testField";
    private static final String TEST_OFFER_CODE = "testOfferCode";

    @InjectMocks
    private OfferService unit;

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
        when(mockCustomValidator.processOfferCreateValidation(any())).thenReturn(null);
        when(mockOfferManager.createOffer(eq(fixtureOfferDTO))).thenReturn(fixtureOfferDTO);

        ResponseEntity<OfferDTO> response = unit.createOffer(fixtureOfferDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fixtureOfferDTO, fixtureOfferDTO);
        verify(mockCustomValidator, only()).processOfferCreateValidation(fixtureOfferDTO);
        verify(mockOfferManager).createOffer(eq(fixtureOfferDTO));
    }

    @Test
    public void testCreateOfferValidationError() {
        OfferValidationDTO validationDTO = new OfferValidationDTO(fixtureOfferDTO);
        Map<String, Set<ErrorMessageDTO>> violations = new HashMap<>();
        violations.put(TEST_FIELD, Sets.newHashSet(new ErrorMessageDTO("ERR1", "ERR1")));
        validationDTO.setConstraintViolations(violations);
        when(mockCustomValidator.processOfferCreateValidation(any())).thenReturn(validationDTO);

        ResponseEntity<OfferDTO> response = unit.createOffer(fixtureOfferDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(mockCustomValidator, only()).processOfferCreateValidation(eq(fixtureOfferDTO));
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
    public void testGetByIdOfferSuccess() {
        Long id = 1L;
        when(mockOfferManager.getOfferById(anyLong())).thenReturn(fixtureOfferDTO);

        ResponseEntity<OfferDTO> result = unit.getOfferById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(mockOfferManager, only()).getOfferById(anyLong());
        verifyNoMoreInteractions(mockOfferManager);
    }

    @Test
    public void testUpdateOfferSuccess() {
        when(mockCustomValidator.processOfferUpdateValidation(any(OfferDTO.class), anyLong())).thenReturn(null);
        when(mockOfferManager.updateOffer(eq(fixtureOfferDTO), anyLong())).thenReturn(fixtureOfferDTO);

        ResponseEntity<OfferDTO> response = unit.updateOffer(1L, fixtureOfferDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fixtureOfferDTO, fixtureOfferDTO);
        verify(mockCustomValidator, only()).processOfferUpdateValidation(eq(fixtureOfferDTO), anyLong());
        verify(mockOfferManager, only()).updateOffer(eq(fixtureOfferDTO), anyLong());
        verifyNoMoreInteractions(mockOfferManager, mockCustomValidator);
    }

    @Test
    public void testUpdateOfferValidationError() {
        OfferValidationDTO validationDTO = new OfferValidationDTO(fixtureOfferDTO);
        Map<String, Set<ErrorMessageDTO>> violations = new HashMap<>();
        violations.put(TEST_FIELD, Sets.newHashSet(new ErrorMessageDTO("ERR1", "ERR1")));
        validationDTO.setConstraintViolations(violations);
        when(mockCustomValidator.processOfferUpdateValidation(any(OfferDTO.class), anyLong())).thenReturn(validationDTO);

        ResponseEntity<OfferDTO> response = unit.updateOffer(1L, fixtureOfferDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        verify(mockCustomValidator, only()).processOfferUpdateValidation(eq(fixtureOfferDTO), anyLong());
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



}