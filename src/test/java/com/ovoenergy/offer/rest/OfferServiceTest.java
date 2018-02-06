package com.ovoenergy.offer.rest;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.google.common.collect.Sets;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.ValidationDTO;
import com.ovoenergy.offer.test.utils.UnitTest;
import com.ovoenergy.offer.validation.CustomValidationProcessor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@UnitTest
public class OfferServiceTest {

    @InjectMocks
    private OfferService unit = new OfferService();

    private static final String TEST_FIELD = "testField";

    @Mock
    private CustomValidationProcessor mockCustomValidator;


    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();


    @Fixture
    public OfferDTO fixtureOfferDTO;

    @Test
    public void testCreateOfferSuccess() {
        ValidationDTO validationDTO = new ValidationDTO(fixtureOfferDTO);
        when(mockCustomValidator.processValidation(any())).thenReturn(validationDTO);

        ResponseEntity<OfferDTO> response = unit.createOffer(fixtureOfferDTO);

        verify(mockCustomValidator, only()).processValidation(eq(fixtureOfferDTO));
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
    }

    @Test
    public void testCreateOfferValidationError() {
        ValidationDTO validationDTO = new ValidationDTO(fixtureOfferDTO);
        Map<String, Set<ErrorMessageDTO>> violations = new HashMap<>();
        violations.put(TEST_FIELD, Sets.newHashSet(new ErrorMessageDTO("ERR1", "ERR1")));
        validationDTO.setConstraintViolations(violations);
        when(mockCustomValidator.processValidation(any())).thenReturn(validationDTO);

        ResponseEntity<OfferDTO> response = unit.createOffer(fixtureOfferDTO);

        verify(mockCustomValidator, only()).processValidation(eq(fixtureOfferDTO));
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
        assertEquals(1, ((ValidationDTO) response.getBody()).getConstraintViolations().get(TEST_FIELD).size());

    }
}