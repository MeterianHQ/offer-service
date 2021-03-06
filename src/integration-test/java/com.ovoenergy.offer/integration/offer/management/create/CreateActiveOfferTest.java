package com.ovoenergy.offer.integration.offer.management.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.ovoenergy.offer.db.entity.ChannelType;
import com.ovoenergy.offer.db.entity.EligibilityCriteriaType;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferType;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.entity.SupplierType;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.dto.OffersServiceURLs;
import com.ovoenergy.offer.integration.mock.config.OfferRepositoryTestConfiguration;
import com.ovoenergy.offer.validation.key.CodeKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OfferRepositoryTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateActiveOfferTest {

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private interface ValidateOfferForCreateInputData {
        // Invalid data
        String TEST_INVALID_CODE = "code*";
        String TEST_INVALID_SUPPLIER = "Amazon1";
        String TEST_INVALID_OFFER_TYPE = "Giftcard1";
        String TEST_INVALID_ELIGIBILITY_CRITERIA = "SSD1";
        String TEST_INVALID_CHANEL = "Email1";
        Long TEST_INVALID_MAX_VALUE = 3334L;
        Long TEST_INVALID_MAX_REDEMPTION = 888888889L;
        Long TEST_INVALID_EXPIRY_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        Long TEST_INVALID_DATE_BEFORE_NOW = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        // Valid data
        String TEST_VALID_DESCRIPTION = "Valid description 100%";
        String TEST_VALID_NAME = "Valid name 100%";
        String TEST_VALID_CODE = "validCODE";
        String TEST_VALID_SUPPLIER = "Amazon";
        String TEST_VALID_OFFER_TYPE = "Giftcard";
        String TEST_VALID_ELIGIBILITY_CRITERIA = "SSD";
        String TEST_VALID_CHANEL = "Email";
        Long TEST_VALID_MAX_VALUE = 333L;
        Long TEST_VALID_MAX_REDEMPTION = 88888888L;
        Long TEST_VALID_DATE_IN_FUTURE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        Long TEST_VALID_EXPIRY_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        Long TEST_VALID_UPDATE_ON_DATE = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    private interface ValidateOfferForCreateViolationConstraintMessages {
        String REQUIRED_FIELD = "This field is required";
        String NOT_NULL_FIELD = "This field cannot be null";
        String PROVIDED_VALUE_NOT_SUPPORTED = "Provided value is not supported";
        String NOT_UNIQUE_OFFER_CODE = "Please choose a unique offer code";
        String INVALID_OFFER_CODE = "An offer code cannot include spaces or special characters";
        String NON_IN_FUTURE_DATE = "Please select a date in the future";
        String OFFER_EXPIRY_DATE_BEFORE_START_DATE = "Offer Expiry Date must be after the Offer Start Date";
        String INPUT_VALUE_FORMAT = "Please enter a number between 1 and 999";
        String INPUT_REDEMPTION_FORMAT = "Please enter a number between 1 and 99999999";
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testValidateOfferForCreate() throws IOException {
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setDescription(ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION);
        offerToValidate.setOfferName(ValidateOfferForCreateInputData.TEST_VALID_NAME);
        offerToValidate.setOfferCode(ValidateOfferForCreateInputData.TEST_INVALID_CODE);
        offerToValidate.setSupplier(ValidateOfferForCreateInputData.TEST_INVALID_SUPPLIER);
        offerToValidate.setOfferType(ValidateOfferForCreateInputData.TEST_INVALID_OFFER_TYPE);
        offerToValidate.setValue(ValidateOfferForCreateInputData.TEST_INVALID_MAX_VALUE.toString());
        offerToValidate.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_INVALID_MAX_REDEMPTION.toString());
        offerToValidate.setEligibilityCriteria(ValidateOfferForCreateInputData.TEST_INVALID_ELIGIBILITY_CRITERIA);
        offerToValidate.setChannel(ValidateOfferForCreateInputData.TEST_INVALID_CHANEL);
        offerToValidate.setStartDate(ValidateOfferForCreateInputData.TEST_INVALID_DATE_BEFORE_NOW);
        offerToValidate.setExpiryDate(ValidateOfferForCreateInputData.TEST_INVALID_EXPIRY_DATE);
        offerToValidate.setIsExpirable(true);
        offerToValidate.setStatus(StatusType.ACTIVE.name());

        HttpEntity<OfferDTO> entity = new HttpEntity<>(offerToValidate);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Fail 400 http", HttpStatus.BAD_REQUEST, response.getStatusCode());

        OfferValidationDTO validationDTO = objectMapper.reader().forType(OfferValidationDTO.class).readValue(response.getBody());

        assertEquals("Input value for field description is invalid", ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION, validationDTO.getDescription());
        assertEquals("Input value for Name field is invalid", ValidateOfferForCreateInputData.TEST_VALID_NAME, validationDTO.getOfferName());
        assertEquals("Input value for Code field is expected", ValidateOfferForCreateInputData.TEST_INVALID_CODE, validationDTO.getOfferCode());
        assertEquals("Input value for Supplier is expected", ValidateOfferForCreateInputData.TEST_INVALID_SUPPLIER, validationDTO.getSupplier());
        assertEquals("Input value for Offer TYPE is incorrect", ValidateOfferForCreateInputData.TEST_INVALID_OFFER_TYPE, validationDTO.getOfferType());
        assertEquals("Input value for Value is incorrect", ValidateOfferForCreateInputData.TEST_INVALID_MAX_VALUE.toString(), validationDTO.getValue());
        assertEquals("Input value for Offer Redemption is incorrect", ValidateOfferForCreateInputData.TEST_INVALID_MAX_REDEMPTION.toString(), validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Eligibility criteria ", ValidateOfferForCreateInputData.TEST_INVALID_ELIGIBILITY_CRITERIA, validationDTO.getEligibilityCriteria());
        assertEquals("Input value for Channel is incorrect", ValidateOfferForCreateInputData.TEST_INVALID_CHANEL, validationDTO.getChannel());
        assertEquals("Input value for START date is incorrect", ValidateOfferForCreateInputData.TEST_INVALID_DATE_BEFORE_NOW, validationDTO.getStartDate());
        assertEquals("Input value for EXPIRY date is incorrect ", ValidateOfferForCreateInputData.TEST_INVALID_EXPIRY_DATE, validationDTO.getExpiryDate());
        assertTrue("No Expiry Date selected value is incorrect", validationDTO.getIsExpirable());
        assertEquals("Input value for Offer TYPE is expected", ValidateOfferForCreateInputData.TEST_INVALID_OFFER_TYPE, validationDTO.getOfferType());
        assertEquals("Input value for Value is expected", ValidateOfferForCreateInputData.TEST_INVALID_MAX_VALUE.toString(), validationDTO.getValue());
        assertEquals("Input value for Offer Redemption is expected", ValidateOfferForCreateInputData.TEST_INVALID_MAX_REDEMPTION.toString(), validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Eligibility criteria is expected", ValidateOfferForCreateInputData.TEST_INVALID_ELIGIBILITY_CRITERIA, validationDTO.getEligibilityCriteria());
        assertEquals("Input value for Channel is expected", ValidateOfferForCreateInputData.TEST_INVALID_CHANEL, validationDTO.getChannel());
        assertEquals("Input value for START date is expected", ValidateOfferForCreateInputData.TEST_INVALID_DATE_BEFORE_NOW, validationDTO.getStartDate());
        assertEquals("Input value for EXPIRY date is expected ", ValidateOfferForCreateInputData.TEST_INVALID_EXPIRY_DATE, validationDTO.getExpiryDate());
        assertTrue("No Expiry Date selected value is expected", validationDTO.getIsExpirable());
        assertEquals("Status of offer is expected", StatusType.ACTIVE.name(), validationDTO.getStatus());

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
        assertTrue("Validation constraints missed error code for >3 digits in offer value field", offerValueAllErrorCodes.contains(CodeKeys.INVALID_VALUE_FORMAT));
        assertTrue("Validation constraints missed error message for >3 digits on offer value field ", offerValueAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.INPUT_VALUE_FORMAT));

        //Checking validation codes and messages for OfferRedemption
        Set<ErrorMessageDTO> offerRedemptionValidations = validationDTO.getConstraintViolations().get("maxOfferRedemptions");
        Set<String> offerRedemptionAllErrorCodes = offerRedemptionValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerRedemptionAllErrorMessages = offerRedemptionValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for >3 digits in offer value field", offerRedemptionAllErrorCodes.contains(CodeKeys.INVALID_REDEMPTION_FORMAT));
        assertTrue("Validation constraints missed error message for >3 digits on offer value field ", offerRedemptionAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.INPUT_REDEMPTION_FORMAT));

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
        offerToValidate.setStatus(null);

        HttpEntity<OfferDTO> entity = new HttpEntity<>(offerToValidate);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Failure 400 Http", HttpStatus.BAD_REQUEST, response.getStatusCode());

        OfferValidationDTO validationDTO = objectMapper.reader().forType(OfferValidationDTO.class).readValue(response.getBody());

        assertNull("Input value for description is expected", validationDTO.getDescription());
        assertNull("Input value for Name field is expected", validationDTO.getOfferName());
        assertNull("Input value for Code field is expected", validationDTO.getOfferCode());
        assertNull("Input value for Supplier is expected", validationDTO.getSupplier());
        assertNull("Input value for Offer TYPE is expected", validationDTO.getOfferType());
        assertNull("Input value for Value is expected", validationDTO.getValue());
        assertNull("Input value for Offer Redemption expected", validationDTO.getMaxOfferRedemptions());
        assertNull("Input value for Eligibility criteria is expected ", validationDTO.getEligibilityCriteria());
        assertNull("Input value for Channel is expected", validationDTO.getChannel());
        assertNull("Input value for START date is expected", validationDTO.getStartDate());
        assertNull("Input value for EXPIRY date is expected ", validationDTO.getExpiryDate());
        assertTrue("No Expiry Date selected value is expected", validationDTO.getIsExpirable());
        assertNull("Status of offer is expected", validationDTO.getStatus());

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
        assertTrue("Validation constraints missed error code for null value in offer name field", offerNameAllErrorCodes.contains(CodeKeys.FIELD_REQUIRED));
        assertTrue("Validation constraints missed error message for  null value in offer name field", offerNameAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.REQUIRED_FIELD));

        //Checking validation codes and messages for OfferRedemption
        Set<ErrorMessageDTO> offerRedemptionValidations = validationDTO.getConstraintViolations().get("maxOfferRedemptions");
        Set<String> offerRedemptionAllErrorCodes = offerRedemptionValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerRedemptionAllErrorMessages = offerRedemptionValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for null value in offer value field", offerRedemptionAllErrorCodes.contains(CodeKeys.FIELD_REQUIRED));
        assertTrue("Validation constraints missed error message for null value on offer value field ", offerRedemptionAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.REQUIRED_FIELD));

        //Checking validation codes and messages for OfferCode
        Set<ErrorMessageDTO> offerCodeValidations = validationDTO.getConstraintViolations().get("offerCode");
        Set<String> offerCodeAllErrorCodes = offerCodeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerCodeAllErrorMessages = offerCodeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code for null value in offer code field", offerCodeAllErrorCodes.contains(CodeKeys.FIELD_REQUIRED));
        assertTrue("Validation constraints missed error message for null value in offer code field", offerCodeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.REQUIRED_FIELD));

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
        assertTrue("Validation constraints missed error code for >3 digits in offer value field", offerValueAllErrorCodes.contains(CodeKeys.FIELD_REQUIRED));
        assertTrue("Validation constraints missed error message for >3 digits on offer value field ", offerValueAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.REQUIRED_FIELD));

        //Checking validation codes and messages for startDate
        Set<ErrorMessageDTO> startDateValidations = validationDTO.getConstraintViolations().get("startDate");
        Set<String> startDateAllErrorCodes = startDateValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> startDateAllErrorMessages = startDateValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if null set for start date ", startDateAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if null set for start date ", startDateAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));

        //Checking validation codes and messages for Status if null
        Set<ErrorMessageDTO> statusOfferValidations = validationDTO.getConstraintViolations().get("status");
        Set<String> statusOfferAllErrorCodes = statusOfferValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> statusOfferAllErrorMessages = statusOfferValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if null set for status ", statusOfferAllErrorCodes.contains(CodeKeys.NOT_NULL_FIELD));
        assertTrue("Validation constraints missed error message if null set for status ", statusOfferAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_NULL_FIELD));
    }

    @Test
    public void testValidateOfferForCreateWithEmptyInputValues() throws IOException {
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setOfferName("");
        offerToValidate.setOfferCode("");
        offerToValidate.setSupplier("");
        offerToValidate.setOfferType("");
        offerToValidate.setValue(ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE.toString());
        offerToValidate.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION.toString());
        offerToValidate.setEligibilityCriteria("");
        offerToValidate.setChannel("");
        offerToValidate.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE);
        offerToValidate.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerToValidate.setIsExpirable(true);
        offerToValidate.setStatus("");

        HttpEntity<OfferDTO> entity = new HttpEntity<>(offerToValidate);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Failure 400 http", HttpStatus.BAD_REQUEST, response.getStatusCode());

        OfferValidationDTO validationDTO = objectMapper.reader().forType(OfferValidationDTO.class).readValue(response.getBody());

        assertEquals("Empty Input value for Name field  expected", "", validationDTO.getOfferName());
        assertEquals("Empty Input value for Code field  expected", "", validationDTO.getOfferCode());
        assertEquals("Empty Input value for Supplier  expected", "", validationDTO.getSupplier());
        assertEquals("Empty Input value for Offer TYPE expected", "", validationDTO.getOfferType());
        assertEquals("Input value for Offer Redemption is expected", ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION.toString(), validationDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Offer Value is expected", ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE.toString(), validationDTO.getValue());
        assertEquals("Empty Input value for Eligibility criteria expected ", "", validationDTO.getEligibilityCriteria());
        assertEquals("Empty Input value for Channel expected", "", validationDTO.getChannel());
        assertEquals("Input value for START date expected", ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE, validationDTO.getStartDate());
        assertEquals("Input value for EXPIRY date expected ", ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE, validationDTO.getExpiryDate());
        assertTrue("No Expiry Date selected value is expected", validationDTO.getIsExpirable());
        assertEquals("Offer status is expected", "", validationDTO.getStatus());

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
        Set<ErrorMessageDTO> offerCodeValidations = validationDTO.getConstraintViolations().get("offerCode");
        Set<String> offerCodeAllErrorCodes = offerCodeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerCodeAllErrorMessages = offerCodeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if offer code field is empty ", offerCodeAllErrorCodes.contains(CodeKeys.FIELD_REQUIRED));
        assertTrue("Validation constraints missed error message if offer code field is empty", offerCodeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.REQUIRED_FIELD));

        //Checking validation codes and messages for offer supplier dropdown
        Set<ErrorMessageDTO> offerSupplierValidations = validationDTO.getConstraintViolations().get("supplier");
        Set<String> offerSupplierAllErrorCodes = offerSupplierValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerSupplierAllErrorMessages = offerSupplierValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if  offer supplier field empty", offerSupplierAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message if offer supplier field empty ", offerSupplierAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));

        //Checking validation codes and messages for offer channel dropdown
        Set<ErrorMessageDTO> offerChannelValidations = validationDTO.getConstraintViolations().get("channel");
        Set<String> offerChannelAllErrorCodes = offerChannelValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerChannelAllErrorMessages = offerChannelValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if offer channel field is empty", offerChannelAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message if offer channel field is empty ", offerChannelAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));

        //Checking validation codes and messages for offer eligibility criteria dropdown
        Set<ErrorMessageDTO> offerEligibilityValidations = validationDTO.getConstraintViolations().get("eligibilityCriteria");
        Set<String> offerEligibilityAllErrorCodes = offerEligibilityValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerEligibilityAllErrorMessages = offerEligibilityValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if eligibility criteria field is empty ", offerEligibilityAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue("Validation constraints missed error message if eligibility criteria field is empty", offerEligibilityAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));

        // Checking validation codes and messages for STATUS empty
        Set<ErrorMessageDTO> statusOfferValidations = validationDTO.getConstraintViolations().get("status");
        Set<String> statusOfferAllErrorCodes = statusOfferValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> statusOfferAllErrorMessages = statusOfferValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue(" Validation constraints missed error code if status empty ", statusOfferAllErrorCodes.contains(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED));
        assertTrue(" Validation constraints missed error message if status empty", statusOfferAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.PROVIDED_VALUE_NOT_SUPPORTED));
    }

    @Test
    public void createOfferSuccess() throws IOException {
        OfferDTO offerToCreate = new OfferDTO();
        offerToCreate.setDescription(ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION);
        offerToCreate.setOfferName(ValidateOfferForCreateInputData.TEST_VALID_NAME);
        offerToCreate.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerToCreate.setSupplier(ValidateOfferForCreateInputData.TEST_VALID_SUPPLIER);
        offerToCreate.setOfferType(ValidateOfferForCreateInputData.TEST_VALID_OFFER_TYPE);
        offerToCreate.setValue(ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE.toString());
        offerToCreate.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION.toString());
        offerToCreate.setEligibilityCriteria(ValidateOfferForCreateInputData.TEST_VALID_ELIGIBILITY_CRITERIA);
        offerToCreate.setChannel(ValidateOfferForCreateInputData.TEST_VALID_CHANEL);
        offerToCreate.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE);
        offerToCreate.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerToCreate.setIsExpirable(true);
        offerToCreate.setStatus(StatusType.ACTIVE.name());

        OfferDBEntity offerDBEntity = prepareForTestValidOfferDBEntity();

        HttpEntity<OfferDTO> entity = new HttpEntity<>(offerToCreate);

        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCase(eq(offerToCreate.getOfferCode()))).thenReturn(null);
        Mockito.when(offerRepository.save(any(OfferDBEntity.class))).thenReturn(offerDBEntity);
        Mockito.when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(new Date(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE));

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Success: Offer created", HttpStatus.OK, response.getStatusCode());

        OfferDTO offerDTO = objectMapper.reader().forType(OfferValidationDTO.class).readValue(response.getBody());
        assertEquals("Offer status set as ACTIVE", StatusType.ACTIVE.name(), offerDTO.getStatus());

        verifyValidOfferDTO(offerDTO);
        Mockito.verify(offerRepository).save(any(OfferDBEntity.class));
        Mockito.verify(jdbcTemplate).queryForObject(any(String.class), any(RowMapper.class));
    }

    @Test
    public void testCreateOfferDuplicatedCodeError() throws IOException {
        OfferDTO offerToCreate = new OfferDTO();
        offerToCreate.setDescription(ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION);
        offerToCreate.setOfferName(ValidateOfferForCreateInputData.TEST_VALID_NAME);
        offerToCreate.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerToCreate.setSupplier(ValidateOfferForCreateInputData.TEST_VALID_SUPPLIER);
        offerToCreate.setOfferType(ValidateOfferForCreateInputData.TEST_VALID_OFFER_TYPE);
        offerToCreate.setValue(ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE.toString());
        offerToCreate.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION.toString());
        offerToCreate.setEligibilityCriteria(ValidateOfferForCreateInputData.TEST_VALID_ELIGIBILITY_CRITERIA);
        offerToCreate.setChannel(ValidateOfferForCreateInputData.TEST_VALID_CHANEL);
        offerToCreate.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE);
        offerToCreate.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerToCreate.setIsExpirable(true);
        offerToCreate.setStatus(StatusType.ACTIVE.name());

        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCase(anyString())).thenReturn(new OfferDBEntity());

        HttpEntity<OfferDTO> entity = new HttpEntity<>(offerToCreate);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);
        assertEquals("Failure 400 http", HttpStatus.BAD_REQUEST, response.getStatusCode());

        OfferValidationDTO validationDTO = objectMapper.reader().forType(OfferValidationDTO.class).readValue(response.getBody());

        Set<ErrorMessageDTO> offerCodeValidations = validationDTO.getConstraintViolations().get("offerCode");
        Set<String> offerCodeAllErrorCodes = offerCodeValidations.stream().map(ErrorMessageDTO::getCode).collect(Collectors.toSet());
        Set<String> offerCodeAllErrorMessages = offerCodeValidations.stream().map(ErrorMessageDTO::getMessage).collect(Collectors.toSet());
        assertTrue("Validation constraints missed error code if offer code field is empty ", offerCodeAllErrorCodes.contains(CodeKeys.NOT_UNIQUE_OFFER_CODE));
        assertTrue("Validation constraints missed error message if offer code field is empty", offerCodeAllErrorMessages.contains(ValidateOfferForCreateViolationConstraintMessages.NOT_UNIQUE_OFFER_CODE));

        Mockito.verify(offerRepository, Mockito.only()).findOneByOfferCodeIgnoreCase(anyString());
    }

    @Test
    public void fetchAllOffersSuccess() {
        OfferDBEntity offerDBEntity = prepareForTestValidOfferDBEntity();
        Mockito.when(offerRepository.findAll()).thenReturn(Lists.newArrayList(offerDBEntity));

        ResponseEntity<List<OfferDTO>> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.GET_ALL_OFFERS),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<OfferDTO>>() {
                });

        assertEquals("Status code is OK", HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void fetchAllOffersNoData() {
        Mockito.when(offerRepository.findAll()).thenReturn(Lists.newArrayList());

        ResponseEntity<List<OfferDTO>> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.GET_ALL_OFFERS),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<OfferDTO>>() {
                });

        assertEquals("Status code is OK", HttpStatus.OK, response.getStatusCode());
        assertEquals("List is empty", 0, response.getBody().size());
    }

    private OfferDBEntity prepareForTestValidOfferDBEntity() {
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
        offerDBEntity.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE);
        offerDBEntity.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerDBEntity.setIsExpirable(true);
        offerDBEntity.setStatus(StatusType.ACTIVE);
        offerDBEntity.setUpdatedOn(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE);
        offerDBEntity.setId(1L);
        return offerDBEntity;
    }

    private void verifyValidOfferDTO(OfferDTO offerDTO) {
        assertEquals("Input value for field description is valid", ValidateOfferForCreateInputData.TEST_VALID_DESCRIPTION, offerDTO.getDescription());
        assertEquals("Input value for Name field is valid", ValidateOfferForCreateInputData.TEST_VALID_NAME, offerDTO.getOfferName());
        assertEquals("Input value for Code field is valid", ValidateOfferForCreateInputData.TEST_VALID_CODE, offerDTO.getOfferCode());
        assertEquals("Input value for Supplier is valid", ValidateOfferForCreateInputData.TEST_VALID_SUPPLIER, offerDTO.getSupplier());
        assertEquals("Input value for Offer TYPE is valid", ValidateOfferForCreateInputData.TEST_VALID_OFFER_TYPE, offerDTO.getOfferType());
        assertEquals("Input value for Value is valid", ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE.toString(), offerDTO.getValue());
        assertEquals("Input value for Offer Redemption is valid", ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION.toString(), offerDTO.getMaxOfferRedemptions());
        assertEquals("Input value for Eligibility criteria is valid ", ValidateOfferForCreateInputData.TEST_VALID_ELIGIBILITY_CRITERIA, offerDTO.getEligibilityCriteria());
        assertEquals("Input value for Channel is valid", ValidateOfferForCreateInputData.TEST_VALID_CHANEL, offerDTO.getChannel());
        assertEquals("Input value for START date is valid", ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE, offerDTO.getStartDate());
        assertEquals("Input value for EXPIRY date is valid ", ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE, offerDTO.getExpiryDate());
        assertNotNull("Id for offer is autogenerated ", offerDTO.getId());
        assertTrue("No Expiry Date is true", offerDTO.getIsExpirable());
        assertEquals("Input value for Update_on Date is valid ", ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE, offerDTO.getUpdatedOn());
        assertEquals("Input value for STATUS is valid ", StatusType.ACTIVE.name(), offerDTO.getStatus());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}



