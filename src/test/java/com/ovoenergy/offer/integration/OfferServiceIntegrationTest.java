package com.ovoenergy.offer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovoenergy.offer.integration.mock.MockApplication;
import com.ovoenergy.offer.db.entity.*;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OffersServiceURLs;
import com.ovoenergy.offer.dto.ValidationDTO;
import com.ovoenergy.offer.integration.mock.config.OfferRepositoryTestConfiguration;
import com.ovoenergy.offer.test.utils.IntegrationTest;
import com.ovoenergy.offer.validation.key.CodeKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Matchers.any;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MockApplication.class, OfferRepositoryTestConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@IntegrationTest
@ActiveProfiles("integrationtest")
public class OfferServiceIntegrationTest {

    @Autowired
    private OfferRepository offerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferServiceIntegrationTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private interface ValidateOfferForCreateInputData {

        // Invalid data
        String TEST_INVALID_CODE = "code*";
        String TEST_INVALID_SUPPLIER = "Amazon1";
        String TEST_INVALID_OFFER_TYPE = "Giftcard1";
        String TEST_INVALID_ELIGIBILITY_CRITERIA = "SSD1";
        String TEST_INVALID_CHANEL = "Email1";
        Long TEST_INVALID_MAX_VALUE = 3334L;
        Long TEST_INVALID_MAX_REDEMPTION= 888888889L;
        Long TEST_INVALID_EXPIRY_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        Long TEST_INVALID_START_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();


        // Valid data
        String TEST_VALID_DESCRIPTION = "Valid description 100%";
        String TEST_VALID_NAME = "Valid name 100%";
        String TEST_VALID_CODE = "validCODE";
        String TEST_VALID_SUPPLIER = "Amazon";
        String TEST_VALID_OFFER_TYPE = "Giftcard";
        String TEST_VALID_ELIGIBILITY_CRITERIA = "SSD";
        String TEST_VALID_CHANEL = "Email";
        Long TEST_VALID_MAX_VALUE = 333L;
        Long TEST_VALID_MAX_REDEMPTION= 88888888L;
        Long TEST_VALID_START_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        Long TEST_VALID_EXPIRY_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    private interface ValidateOfferForCreateViolationConstraintMessages {
        String REQUIRED_FIELD = "This field is required";
        String NOT_NULL_FIELD = "This field cannot be null";
        String PROVIDED_VALUE_NOT_SUPPORTED = "Provided value is not supported";
        String INPUT_VALUE_ZERO= "Input value cannot be 0";
        String NOT_UNIQUE_OFFER_CODE= "Please choose a unique offer code";
        String INVALID_OFFER_CODE="An offer code cannot include spaces or special characters";
        String NON_IN_FUTURE_DATE="Please select a date in the future";
        String OFFER_EXPIRY_DATE_BEFORE_START_DATE= "Offer Expiry Date must be after the Offer Start Date";
        String NO_EXPIRITY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE="No expiry date' cannot be ticked if 'Expiry date' selected";
        String INPUT_VALUE_MAX = "Field value is limited to 3 digits";
        String INPUT_REDEMPTION_MAX = "Field value is limited to 8 digits";
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
        offerToValidate.setOfferCode(ValidateOfferForCreateInputData.TEST_INVALID_CODE);
        offerToValidate.setSupplier(ValidateOfferForCreateInputData.TEST_INVALID_SUPPLIER);
        offerToValidate.setOfferType(ValidateOfferForCreateInputData.TEST_INVALID_OFFER_TYPE);
        offerToValidate.setValue(ValidateOfferForCreateInputData.TEST_INVALID_MAX_VALUE);
        offerToValidate.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_INVALID_MAX_REDEMPTION);
        offerToValidate.setEligibilityCriteria(ValidateOfferForCreateInputData.TEST_INVALID_ELIGIBILITY_CRITERIA);
        offerToValidate.setChannel(ValidateOfferForCreateInputData.TEST_INVALID_CHANEL);
        offerToValidate.setStartDate(ValidateOfferForCreateInputData.TEST_INVALID_START_DATE);
        offerToValidate.setExpiryDate(ValidateOfferForCreateInputData.TEST_INVALID_EXPIRY_DATE);
        offerToValidate.setIsExpirable(true);

        HttpEntity<OfferDTO> entity = new HttpEntity<OfferDTO>(offerToValidate, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Fail 400 http", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ValidationDTO validationDTO = objectMapper.reader().forType(ValidationDTO.class).readValue(response.getBody());

        assertEquals("Input value for field description is different", ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION, validationDTO.getDescription());
        assertEquals("Input value for Name field is incorrect",ValidateOfferForCreateInputData.TEST_VALID_NAME,validationDTO.getOfferName());
        assertEquals("Input value for Code field is incorrect",ValidateOfferForCreateInputData.TEST_INVALID_CODE,validationDTO.getOfferCode());
        assertEquals("Input value for Supplier is incorrect",ValidateOfferForCreateInputData.TEST_INVALID_SUPPLIER,validationDTO.getSupplier());
        assertEquals("Input value for Offer TYPE is incorrect",ValidateOfferForCreateInputData.TEST_INVALID_OFFER_TYPE,validationDTO.getOfferType());
        assertEquals("Input value for Value is incorrect",ValidateOfferForCreateInputData.TEST_INVALID_MAX_VALUE,validationDTO.getValue());
        assertEquals("Input value for Offer Redeptions is incorrect", ValidateOfferForCreateInputData.TEST_INVALID_MAX_REDEMPTION,validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Eligibility criteria ",ValidateOfferForCreateInputData.TEST_INVALID_ELIGIBILITY_CRITERIA,validationDTO.getEligibilityCriteria());
        assertEquals("Input value for Channel is incorrect",ValidateOfferForCreateInputData.TEST_INVALID_CHANEL,validationDTO.getChannel());
        assertEquals("Input value for START date is incorrect",ValidateOfferForCreateInputData.TEST_INVALID_START_DATE, validationDTO.getStartDate());
        assertEquals("Input value for EXPIRY date is incorrect ",ValidateOfferForCreateInputData.TEST_INVALID_EXPIRY_DATE, validationDTO.getExpiryDate());
        assertTrue("No Expiry Date selected value is incorrect",validationDTO.getIsExpirable());

        //Checking validation codes and messages for expiryDate
        Set<ErrorMessageDTO> expiryDateValidations = validationDTO.getConstraintViolations().get("expiryDate");
        Set<String> expiryDateAllErrorCodes = expiryDateValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> expiryDateAllErrorMessages = expiryDateValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for expiry date before start", expiryDateAllErrorCodes.contains(CodeKeys.OFFER_EXPIRY_DATE_BEFORE_START_DATE));
        assertTrue("Validation constraints missed error message for expiry date before start", expiryDateAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.OFFER_EXPIRY_DATE_BEFORE_START_DATE));
        assertTrue("Validation constraints missed error code for expiry date NON in future", expiryDateAllErrorCodes.contains(CodeKeys.NON_IN_FUTURE_DATE));
        assertTrue("Validation constraints missed error message for expiry date NON in future", expiryDateAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NON_IN_FUTURE_DATE));

        //Checking validation codes and messages for startDate
        Set<ErrorMessageDTO> startDateValidations = validationDTO.getConstraintViolations().get("startDate");
        Set<String> startDateAllErrorCodes = startDateValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> startDateAllErrorMessages = startDateValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for start date NON in future", startDateAllErrorCodes.contains(CodeKeys.NON_IN_FUTURE_DATE));
        assertTrue("Validation constraints missed error message for start date NON in future", startDateAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NON_IN_FUTURE_DATE));


        //Checking validation codes and messages for OfferCode
        Set<ErrorMessageDTO> offerCodeValidations = validationDTO.getConstraintViolations().get("offerCode");
        Set<String> offerCodeAllErrorCodes = offerCodeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerCodeAllErrorMessages = offerCodeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for invalid offer code", offerCodeAllErrorCodes.contains(CodeKeys.INVALID_OFFER_CODE));
        assertTrue("Validation constraints missed error message for invalid offer code", offerCodeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.INVALID_OFFER_CODE));

        //Checking validation codes and messages for OfferValue
        Set<ErrorMessageDTO> offerValueValidations = validationDTO.getConstraintViolations().get("value");
        Set<String> offerValueAllErrorCodes = offerValueValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerValueAllErrorMessages = offerValueValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for >3 digits in offer value field", offerValueAllErrorCodes.contains(CodeKeys.INPUT_VALUE_MAX));
        assertTrue("Validation constraints missed error message for >3 digits on offer value field ", offerValueAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.INPUT_VALUE_MAX));


        //Checking validation codes and messages for OfferRedemption
        Set<ErrorMessageDTO> offerRedemptionValidations = validationDTO.getConstraintViolations().get("maxOfferRedemptions");
        Set<String> offerRedemptionAllErrorCodes = offerRedemptionValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerRedemptionAllErrorMessages = offerRedemptionValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for >3 digits in offer value field", offerRedemptionAllErrorCodes.contains(CodeKeys.INPUT_REDEMPTION_MAX));
        assertTrue("Validation constraints missed error message for >3 digits on offer value field ", offerRedemptionAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.INPUT_REDEMPTION_MAX));

        //Checking validation codes and messages for offer supplier dropdown
        Set<ErrorMessageDTO> offerSupplierValidations = validationDTO.getConstraintViolations().get("supplier");
        Set<String> offerSupplierAllErrorCodes = offerSupplierValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerSupplierAllErrorMessages = offerSupplierValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if not supported value sent in offer supplier", offerSupplierAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message if not supported value in offer supplier ", offerSupplierAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));


        //Checking validation codes and messages for offer type dropdown
        Set<ErrorMessageDTO> offerTypeValidations = validationDTO.getConstraintViolations().get("offerType");
        Set<String> offerTypeAllErrorCodes = offerTypeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerTypeAllErrorMessages = offerTypeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if not supported value sent in offer type", offerTypeAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message iif not supported value in offer type ", offerTypeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));


        //Checking validation codes and messages for offer eligibility criteria dropdown
        Set<ErrorMessageDTO> offerEligibilityValidations = validationDTO.getConstraintViolations().get("eligibilityCriteria");
        Set<String> offerEligibilityAllErrorCodes = offerEligibilityValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerEligibilityAllErrorMessages = offerEligibilityValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if not supported value sent in offer eligibility criteria", offerEligibilityAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message iif not supported value in eligibility criteria", offerEligibilityAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));

        //Checking validation codes and messages for offer channel dropdown
        Set<ErrorMessageDTO> offerChannelValidations = validationDTO.getConstraintViolations().get("channel");
        Set<String> offerChannelAllErrorCodes = offerChannelValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerChannelAllErrorMessages = offerChannelValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if not supported value sent in offer channel", offerChannelAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message iif not supported value in offer channel ", offerChannelAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));
    }

    @Test
    public void testValidateOfferForCreateWithNullInputValues() throws IOException {
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setIsExpirable(true);

        HttpEntity<OfferDTO> entity = new HttpEntity<OfferDTO>(offerToValidate, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Failure 400 Http", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ValidationDTO validationDTO = objectMapper.reader().forType(ValidationDTO.class).readValue(response.getBody());

      // assertEquals("Input value for field description is null", validationDTO.getDescription());
        assertEquals("Input value for Name field is null", null,validationDTO.getOfferName());
        assertEquals("Input value for Code field is null",null,validationDTO.getOfferCode());
        assertEquals("Input value for Supplier is null",null,validationDTO.getSupplier());
        assertEquals("Input value for Offer TYPE is null",null,validationDTO.getOfferType());
        assertEquals("Input value for Value is null",null,validationDTO.getValue());
        assertEquals("Input value for Offer Redemptions null",null,validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Eligibility criteria is null ",null, validationDTO.getEligibilityCriteria());
        assertEquals("Input value for Channel is null",null, validationDTO.getChannel());
        assertEquals("Input value for START date is null", null, validationDTO.getStartDate());
        assertEquals("Input value for EXPIRY date is null ", null, validationDTO.getExpiryDate());
        assertTrue("No Expiry Date selected value is incorrect",validationDTO.getIsExpirable());

        //Checking validation codes and messages for OfferType
        Set<ErrorMessageDTO> offerTypeValidations = validationDTO.getConstraintViolations().get("offerType");
        Set<String> offerTypeAllErrorCodes = offerTypeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerTypeAllErrorMessages = offerTypeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for null value in offer name field", offerTypeAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message for  null value in offer name field", offerTypeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));


        //Checking validation codes and messages for OfferName
        Set<ErrorMessageDTO> offerNameValidations = validationDTO.getConstraintViolations().get("offerName");
        Set<String> offerNameAllErrorCodes = offerNameValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerNameAllErrorMessages = offerNameValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for null value in offer name field", offerNameAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message for  null value in offer name field", offerNameAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for OfferRedemption
        Set<ErrorMessageDTO> offerRedemptionValidations = validationDTO.getConstraintViolations().get("maxOfferRedemptions");
        Set<String> offerRedemptionAllErrorCodes = offerRedemptionValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerRedemptionAllErrorMessages = offerRedemptionValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for null value in offer value field", offerRedemptionAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message for null value on offer value field ", offerRedemptionAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for OfferCode
        Set<ErrorMessageDTO> offerCodeValidations = validationDTO.getConstraintViolations().get("offerCode");
        Set<String> offerCodeAllErrorCodes = offerCodeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerCodeAllErrorMessages = offerCodeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for null value in offer code field", offerCodeAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message for null value in offer code field", offerCodeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for offer supplier dropdown
        Set<ErrorMessageDTO> offerSupplierValidations = validationDTO.getConstraintViolations().get("supplier");
        Set<String> offerSupplierAllErrorCodes = offerSupplierValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerSupplierAllErrorMessages = offerSupplierValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if null value in offer supplier field", offerSupplierAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if null value in offer supplier field ", offerSupplierAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for offer channel dropdown
        Set<ErrorMessageDTO> offerChannelValidations = validationDTO.getConstraintViolations().get("channel");
        Set<String> offerChannelAllErrorCodes = offerChannelValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerChannelAllErrorMessages = offerChannelValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if null value in offer channel field", offerChannelAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if null value in offer channel field ", offerChannelAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for offer eligibility criteria dropdown
        Set<ErrorMessageDTO> offerEligibilityValidations = validationDTO.getConstraintViolations().get("eligibilityCriteria");
        Set<String> offerEligibilityAllErrorCodes = offerEligibilityValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerEligibilityAllErrorMessages = offerEligibilityValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if null value in offer eligibility criteria ", offerEligibilityAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if null in eligibility criteria", offerEligibilityAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for OfferValue
        Set<ErrorMessageDTO> offerValueValidations = validationDTO.getConstraintViolations().get("value");
        Set<String> offerValueAllErrorCodes = offerValueValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerValueAllErrorMessages = offerValueValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for >3 digits in offer value field", offerValueAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message for >3 digits on offer value field ", offerValueAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for startDate
        Set<ErrorMessageDTO> startDateValidations = validationDTO.getConstraintViolations().get("startDate");
        Set<String> startDateAllErrorCodes = startDateValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> startDateAllErrorMessages = startDateValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if null set for start date ", startDateAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if null set for start date ", startDateAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

    }

    //@Test
    public void testValidateOfferForCreateWithEmptyInputValues() throws IOException {
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setOfferName("");
        offerToValidate.setOfferCode("");
        offerToValidate.setSupplier("");
        offerToValidate.setOfferType("");
        offerToValidate.setValue(3L);
        offerToValidate.setMaxOfferRedemptions(3L);
        offerToValidate.setEligibilityCriteria("");
        offerToValidate.setChannel("");
        offerToValidate.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_START_DATE);
        offerToValidate.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerToValidate.setIsExpirable(true);

        HttpEntity<OfferDTO> entity = new HttpEntity<OfferDTO>(offerToValidate, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Failure 400http", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ValidationDTO validationDTO = objectMapper.reader().forType(ValidationDTO.class).readValue(response.getBody());

        assertEquals("Input value for Name field is empty","", validationDTO.getOfferName());
        assertEquals("Input value for Code field is incorrec","",validationDTO.getOfferCode());
        assertEquals("Input value for Supplier is incorrect","",validationDTO.getSupplier());
        assertEquals("Input value for Offer TYPE is incorrect","",validationDTO.getOfferType());
        assertEquals("Input value for Offer Redeptions is correct", Long.valueOf(3L),validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Offer Redeptions is incorrect",Long.valueOf(3L),validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Eligibility criteria ","",validationDTO.getEligibilityCriteria());
        assertEquals("Input value for Channel is incorrect","",validationDTO.getChannel());
        assertEquals("Input value for START date is incorrect",ValidateOfferForCreateInputData.TEST_VALID_START_DATE, validationDTO.getStartDate());
        assertEquals("Input value for EXPIRY date is incorrect ",ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE, validationDTO.getExpiryDate());
        assertTrue("No Expiry Date selected value is incorrect",validationDTO.getIsExpirable());

        //Checking validation codes and messages for offer type dropdown
        Set<ErrorMessageDTO> offerTypeValidations = validationDTO.getConstraintViolations().get("offerType");
        Set<String> offerTypeAllErrorCodes = offerTypeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerTypeAllErrorMessages = offerTypeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if offer type is Empty", offerTypeAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message if offer type is empty ", offerTypeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));

        //Checking validation codes and messages for OfferName
        Set<ErrorMessageDTO> offerNameValidations = validationDTO.getConstraintViolations().get("offerName");
        Set<String> offerNameAllErrorCodes = offerNameValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerNameAllErrorMessages = offerNameValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if name field is Empty", offerNameAllErrorCodes.contains(CodeKeys.FIELD_REQUIRED));
        assertTrue("Validation constraints missed error message name field is empty", offerNameAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.REQUIRED_FIELD));

        //Checking validation codes and messages for OfferCode
        //Set<ErrorMessageDTO> offerCodeValidations = validationDTO.getConstraintViolations().get("offerCode");
        //Set<String> offerCodeAllErrorCodes = offerCodeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
      //  Set<String> offerCodeAllErrorMessages = offerCodeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
      //  assertTrue("Validation constraints missed error code if offer code field is empty ", offerCodeAllErrorCodes.contains(CodeKeys.FIELD_REQUIRED));
       // assertTrue("Validation constraints missed error message if offer code field is empty", offerCodeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.REQUIRED_FIELD));

        //Checking validation codes and messages for OfferRedemption
        Set<ErrorMessageDTO> offerRedemptionValidations = validationDTO.getConstraintViolations().get("offerRedemption");
        Set<String> offerRedemptionAllErrorCodes = offerRedemptionValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerRedemptionAllErrorMessages = offerRedemptionValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if offer value field is empty", offerRedemptionAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if offer value field is empty", offerRedemptionAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for offer supplier dropdown
        Set<ErrorMessageDTO> offerSupplierValidations = validationDTO.getConstraintViolations().get("offerSupplier");
        Set<String> offerSupplierAllErrorCodes = offerSupplierValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerSupplierAllErrorMessages = offerSupplierValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if  offer supplier field empty", offerSupplierAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if offer supplier field empty ", offerSupplierAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for offer channel dropdown
        Set<ErrorMessageDTO> offerChannelValidations = validationDTO.getConstraintViolations().get("offerChannel");
        Set<String> offerChannelAllErrorCodes = offerChannelValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerChannelAllErrorMessages = offerChannelValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if offer channel field is empty", offerChannelAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if offer channel field is empty ", offerChannelAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for offer eligibility criteria dropdown
        Set<ErrorMessageDTO> offerEligibilityValidations = validationDTO.getConstraintViolations().get("offerEligibility");
        Set<String> offerEligibilityAllErrorCodes = offerEligibilityValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerEligibilityAllErrorMessages = offerEligibilityValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if eligibility criteria field is empty ", offerEligibilityAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message if eligibility criteria field is empty", offerEligibilityAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));


        //Checking validation codes and messages for OfferValue
        //Set<ErrorMessageDTO> offerValueValidations = validationDTO.getConstraintViolations().get("offerValue");
       // Set<String> offerValueAllErrorCodes = offerValueValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        //Set<String> offerValueAllErrorMessages = offerValueValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        //assertTrue("Validation constraints missed error code if offer value field is empty", offerValueAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        //assertTrue("Validation constraints missed error message if offer value field is empty ", offerValueAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for startDate
        //Set<ErrorMessageDTO> startDateValidations = validationDTO.getConstraintViolations().get("startDate");
        //Set<String> startDateAllErrorCodes = startDateValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        //Set<String> startDateAllErrorMessages = startDateValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        //assertTrue("Validation constraints missed error code if start date is empty", startDateAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
       // assertTrue("Validation constraints missed error message if start date is empty", startDateAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

    }

    @Test
    public void createOfferSuccess() {
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setDescription(ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION);
        offerToValidate.setOfferName(ValidateOfferForCreateInputData.TEST_VALID_NAME);
        offerToValidate.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerToValidate.setSupplier(ValidateOfferForCreateInputData.TEST_VALID_SUPPLIER);
        offerToValidate.setOfferType(ValidateOfferForCreateInputData.TEST_VALID_OFFER_TYPE);
        offerToValidate.setValue(ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE);
        offerToValidate.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION);
        offerToValidate.setEligibilityCriteria(ValidateOfferForCreateInputData.TEST_VALID_ELIGIBILITY_CRITERIA);
        offerToValidate.setChannel(ValidateOfferForCreateInputData.TEST_VALID_CHANEL);
        offerToValidate.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_START_DATE);
        offerToValidate.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerToValidate.setIsExpirable(true);

        OfferDBEntity offerDBEntity = new OfferDBEntity();
        offerDBEntity.setDescription(ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION);
        offerDBEntity.setOfferName(ValidateOfferForCreateInputData.TEST_VALID_NAME);
        offerDBEntity.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerDBEntity.setSupplier(SupplierType.byValue(ValidateOfferForCreateInputData.TEST_VALID_SUPPLIER));
        offerDBEntity.setOfferType(OfferType.byValue(ValidateOfferForCreateInputData.TEST_VALID_OFFER_TYPE));
        offerDBEntity.setValue(ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE);
        offerDBEntity.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION);
        offerDBEntity.setEligibilityCriteria(EligibilityCriteriaType.byValue(ValidateOfferForCreateInputData.TEST_VALID_ELIGIBILITY_CRITERIA));
        offerDBEntity.setChannel(ChannelType.byValue(ValidateOfferForCreateInputData.TEST_VALID_CHANEL));
        offerDBEntity.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_START_DATE);
        offerDBEntity.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerDBEntity.setIsExpirable(true);
        offerDBEntity.setId(1L);

        HttpEntity<OfferDTO> entity = new HttpEntity<OfferDTO>(offerToValidate, headers);

        Mockito.when(offerRepository.save(any(OfferDBEntity.class))).thenReturn(offerDBEntity);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Success: Offer created", HttpStatus.OK, response.getStatusCode());

        ValidationDTO validationDTO = null;
        try {
            validationDTO = objectMapper.reader().forType(ValidationDTO.class).readValue(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals("Input value for field description is valid", ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION, validationDTO.getDescription());
        assertEquals("Input value for Name field is valid",ValidateOfferForCreateInputData.TEST_VALID_NAME,validationDTO.getOfferName());
        assertEquals("Input value for Code field is valid",ValidateOfferForCreateInputData.TEST_VALID_CODE,validationDTO.getOfferCode());
        assertEquals("Input value for Supplier is valid",ValidateOfferForCreateInputData.TEST_VALID_SUPPLIER,validationDTO.getSupplier());
        assertEquals("Input value for Offer TYPE is valid",ValidateOfferForCreateInputData.TEST_VALID_OFFER_TYPE,validationDTO.getOfferType());
        assertEquals("Input value for Value is valid",ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE,validationDTO.getValue());
        assertEquals("Input value for Offer Redeptions is valid",ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION,validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Eligibility criteria is valid ",ValidateOfferForCreateInputData.TEST_VALID_ELIGIBILITY_CRITERIA,validationDTO.getEligibilityCriteria());
        assertEquals("Input value for Channel is valid",ValidateOfferForCreateInputData.TEST_VALID_CHANEL,validationDTO.getChannel());
        assertEquals("Input value for START date is valid",ValidateOfferForCreateInputData.TEST_VALID_START_DATE, validationDTO.getStartDate());
        assertEquals("Input value for EXPIRY date is valid ",ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE, validationDTO.getExpiryDate());
        assertNotNull("Id for offer is autogenerated ", validationDTO.getId());
        assertTrue("No Expiry Date is true",validationDTO.getIsExpirable());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
