package com.ovoenergy.offer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.ovoenergy.offer.Application;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OffersServiceURLs;
import com.ovoenergy.offer.dto.ValidationDTO;
import com.ovoenergy.offer.rest.InternalExceptionHandler;
import com.ovoenergy.offer.test.utils.IntegrationTest;
import com.ovoenergy.offer.validation.key.CodeKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ovoenergy.offer.validation.key.CodeKeys.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@IntegrationTest
/*
@TestPropertySource(properties = {"management.port=0"})
*/
public class OfferServiceIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferServiceIntegrationTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private interface ValidateOfferForCreateInputData {
        final static String TEST_VALID_DESCRIPTION = "description";
        final static String TEST_VALID_NAME = "name";
        final static String TEST_VALID_CODE = "code";
        final static String TEST_VALID_SUPPLIER = "Amazon";
        final static String TEST_VALID_OFFER_TYPE = "Giftcard";
        final static String TEST_VALID_ELIGIBILITY_CRITERIA = "SSD";
        final static String TEST_VALID_CHANEL = "Email";
        static final Long TEST_VALID_START_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        static final Long TEST_INVALID_EXPIRY_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    private interface ValidateOfferForCreateViolationConstraintMessages {
        final static String REQUIRED_FIELD = "This field is required";
        final static String NOT_NULL_FIELD = "This field cannot be null";
        final static String PROVIDED_VALUE_NOT_SUPPORTED = "Provided value is not supported";
        final static String INPUT_VALUE_ZERO= "Input value cannot be 0";
        final static String NOT_UNIQUE_OFFER_CODE= "Please choose a unique offer code";
        final static String INVALID_OFFER_CODE="An offer code cannot include spaces or special characters";
        final static String NON_IN_FUTURE_DATE="Please select a date in the future";
        final static String OFFER_EXPIRY_DATE_BEFORE_START_DATE= "Offer Expiry Date must be after the Offer Start Date";
        final static String NO_EXPIRITY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE="No expiry date' cannot be ticked if 'Expiry date' selected";
    }

    @LocalServerPort
    private int port;

    private  TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    public void testValidateOfferForCreate() throws IOException {
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setDescription(ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION);
        offerToValidate.setOfferName(ValidateOfferForCreateInputData.TEST_VALID_NAME);
        offerToValidate.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerToValidate.setSupplier(ValidateOfferForCreateInputData.TEST_VALID_SUPPLIER);
        offerToValidate.setOfferType(ValidateOfferForCreateInputData.TEST_VALID_OFFER_TYPE);
        offerToValidate.setValue(3L);
        offerToValidate.setMaxOfferRedemptions(3L);
        offerToValidate.setEligibilityCriteria(ValidateOfferForCreateInputData.TEST_VALID_ELIGIBILITY_CRITERIA);
        offerToValidate.setChannel(ValidateOfferForCreateInputData.TEST_VALID_CHANEL);
        offerToValidate.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_START_DATE);
        offerToValidate.setExpiryDate(ValidateOfferForCreateInputData.TEST_INVALID_EXPIRY_DATE);
        offerToValidate.setIsExpirable(true);

        HttpEntity<OfferDTO> entity = new HttpEntity<OfferDTO>(offerToValidate, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ValidationDTO validationDTO = objectMapper.reader().forType(ValidationDTO.class).readValue(response.getBody());

        assertEquals("Input value for field description is different", ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION, validationDTO.getDescription());
        assertEquals("Input value for Name field is incorrect",ValidateOfferForCreateInputData.TEST_VALID_NAME,validationDTO.getOfferName());
        assertEquals("Input value for Code field is incorrect",ValidateOfferForCreateInputData.TEST_VALID_CODE,validationDTO.getOfferCode());
        assertEquals("Input value for Supplier is incorrect",ValidateOfferForCreateInputData.TEST_VALID_SUPPLIER,validationDTO.getSupplier());
        assertEquals("Input value for Offer TYPE is incorrect",ValidateOfferForCreateInputData.TEST_VALID_OFFER_TYPE,validationDTO.getOfferType());
        assertEquals("Input value for Value is incorrect",Long.valueOf(3L),(validationDTO.getValue()));
        assertEquals("Input value for Offer Redeptions is incorrect",Long.valueOf(3L),validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Eligibility criteria ",ValidateOfferForCreateInputData.TEST_VALID_ELIGIBILITY_CRITERIA,validationDTO.getEligibilityCriteria());
        assertEquals("Input value for Channel is incorrect",ValidateOfferForCreateInputData.TEST_VALID_CHANEL,validationDTO.getChannel());
        assertEquals("Input value for START date is incorrect",ValidateOfferForCreateInputData.TEST_VALID_START_DATE, validationDTO.getStartDate());
        assertEquals("Input value for EXPIRY date is incorrect ",ValidateOfferForCreateInputData.TEST_INVALID_EXPIRY_DATE, validationDTO.getExpiryDate());
        assertTrue("No Expiry Date selected value is incorrect",validationDTO.getIsExpirable());

        //Checking validation codes and messages for expiryDate
        Set<ErrorMessageDTO> expiryDateValidations = validationDTO.getConstraintViolations().get("expiryDate");
        Set<String> expiryDateAllErrorCodes = expiryDateValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> expiryDateAllErrorMessages = expiryDateValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for expiry date before start", expiryDateAllErrorCodes.contains(CodeKeys.OFFER_EXPIRY_DATE_BEFORE_START_DATE));
        assertTrue("Validation constraints missed error message for expiry date before start", expiryDateAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.OFFER_EXPIRY_DATE_BEFORE_START_DATE));

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
