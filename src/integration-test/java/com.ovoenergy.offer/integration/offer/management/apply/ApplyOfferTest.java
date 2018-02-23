package com.ovoenergy.offer.integration.offer.management.apply;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.ovoenergy.offer.db.entity.ChannelType;
import com.ovoenergy.offer.db.entity.EligibilityCriteriaType;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.OfferType;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.entity.SupplierType;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.dto.OfferVerifyDTO;
import com.ovoenergy.offer.dto.OffersServiceURLs;
import com.ovoenergy.offer.db.entity.StatusType;
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
import org.springframework.http.HttpHeaders;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OfferRepositoryTestConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplyOfferTest {

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OfferRedeemRepository offerRedeemRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private interface ValidateOfferForCreateInputData {

        // Invalid data
        String TEST_INVALID_CODE = "code*";
        Long TEST_INVALID_EXPIRY_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        Long TEST_INVALID_DATE_BEFORE_NOW = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        String TEST_NON_EXISTING_CODE = "nonexisting";

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



        //valid code for Q&S flow:
        String TEST_VALID_EMAIL = "valid.email@mail.ru";
        Long TEST_ACTUAL_REDEMTION = 1L;
        Long TEST_OFFER_ID_REDEEMED = 1L;
    }

    private interface ValidateOfferForCreateViolationConstraintMessages {

        String INVALID_EMAIL = "Email format is not valid";
        String OFFER_EXPIRED = "Offer has expired";
        String OFFER_INVALID = "Offer code invalid";
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    public void verifyOfferValidationCases() throws IOException {
        Mockito.when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(new Date(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE));
        // test incorrect offer code format shouldn't pass validation
        OfferVerifyDTO offerToVerify = new OfferVerifyDTO(ValidateOfferForCreateInputData.TEST_INVALID_CODE);
        processInvalidOfferValidation(offerToVerify);

        // test empty offer code shouldn't pass validation
        offerToVerify.setOfferCode("");
        processInvalidOfferValidation(offerToVerify);

        // test null offer code shouldn't pass validation
        offerToVerify.setOfferCode(null);
        processInvalidOfferValidation(offerToVerify);

        // test non existing offer code shouldn't pass validation
        offerToVerify.setOfferCode(ValidateOfferForCreateInputData.TEST_NON_EXISTING_CODE);
        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_NON_EXISTING_CODE), eq(StatusType.ACTIVE))).thenReturn(null);
        processInvalidOfferValidation(offerToVerify);

        // test non started offer code shouldn't pass validation
        OfferDBEntity offerDBEntityNotStarted = new OfferDBEntity();
        offerDBEntityNotStarted.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE);
        offerDBEntityNotStarted.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerDBEntityNotStarted.setStatus(StatusType.ACTIVE);
        offerToVerify.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE), eq(StatusType.ACTIVE))).thenReturn(offerDBEntityNotStarted);
        processInvalidOfferValidation(offerToVerify);
        Mockito.verify(offerRepository).findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE), eq(StatusType.ACTIVE));

        // test expired offer shouldn't pass validation
        offerToVerify.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        OfferDBEntity offerDBEntity = new OfferDBEntity();
        offerDBEntity.setStartDate(ValidateOfferForCreateInputData.TEST_INVALID_DATE_BEFORE_NOW);
        offerDBEntity.setExpiryDate(ValidateOfferForCreateInputData.TEST_INVALID_EXPIRY_DATE);
        offerDBEntity.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerDBEntity.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION);
        offerDBEntity.setActualOfferRedemptions(ValidateOfferForCreateInputData.TEST_ACTUAL_REDEMTION);
        offerDBEntity.setIsExpirable(true);
        offerDBEntity.setStatus(StatusType.ACTIVE);

        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE), eq(StatusType.ACTIVE))).thenReturn(offerDBEntity);

        HttpEntity<OfferVerifyDTO> request = new HttpEntity<>(offerToVerify, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.VERIFY_OFFER),
                HttpMethod.POST, request, String.class);

        assertEquals("Bad request status code was not returned", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorMessageDTO messageDTO = objectMapper.reader().forType(ErrorMessageDTO.class).readValue(response.getBody());

        assertEquals("Offer expired error code missed in response", CodeKeys.OFFER_EXPIRED, messageDTO.getCode());
        assertEquals("Offer expired error message missed in response", ValidateOfferForCreateViolationConstraintMessages.OFFER_EXPIRED, messageDTO.getMessage());
        Mockito.verify(offerRepository, Mockito.times(2)).findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE), eq(StatusType.ACTIVE));
    }

    @Test
    public void verifyOfferSuccessCase() throws IOException {
        // test valid offer code should be stored and redeemed
        OfferVerifyDTO offerToCreate = new OfferVerifyDTO(ValidateOfferForCreateInputData.TEST_VALID_CODE);

        OfferDBEntity offerDBEntity = new OfferDBEntity();
        offerDBEntity.setStartDate(ValidateOfferForCreateInputData.TEST_INVALID_DATE_BEFORE_NOW);
        offerDBEntity.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerDBEntity.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerDBEntity.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION);
        offerDBEntity.setActualOfferRedemptions(ValidateOfferForCreateInputData.TEST_ACTUAL_REDEMTION);
        offerDBEntity.setIsExpirable(false);
        offerDBEntity.setStatus(StatusType.ACTIVE);

        Mockito.when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(new Date(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE));
        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE), eq(StatusType.ACTIVE))).thenReturn(offerDBEntity);

        HttpEntity<OfferVerifyDTO> request = new HttpEntity<>(offerToCreate, headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.VERIFY_OFFER),
                HttpMethod.POST, request, Boolean.class);

        assertEquals("OK status code was not returned", HttpStatus.OK, response.getStatusCode());

        assertTrue("Offer was successfully verified", response.getBody());

        Mockito.verify(offerRepository).findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE), eq(StatusType.ACTIVE));
        Mockito.when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(new Date(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE));
    }

    @Test
    public void applyToOfferValidationCases() {
        //test invalid format of email
        OfferApplyDTO offerToApply = new OfferApplyDTO();
        OfferDBEntity offerDBEntity = new OfferDBEntity();

        offerToApply.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        HttpEntity<OfferApplyDTO> request = new HttpEntity<>(offerToApply, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.APPLY_TO_OFFER),
                HttpMethod.POST, request, String.class);

        assertEquals("Bad request status code was not returned", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorMessageDTO messageDTO = null;
        try {
            messageDTO = objectMapper.reader().forType(ErrorMessageDTO.class).readValue(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals("Email format is not valid", CodeKeys.INVALID_EMAIL, messageDTO.getCode());
        assertEquals("Offer expired error message missed in response", ValidateOfferForCreateViolationConstraintMessages.INVALID_EMAIL, messageDTO.getMessage());
    }

    @Test
    public void applyToOfferSuccessCase() throws IOException {
        // test valid offer code should be stored and redeemed
        OfferApplyDTO offerApplyDTO = new OfferApplyDTO();
        offerApplyDTO.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerApplyDTO.setEmail(ValidateOfferForCreateInputData.TEST_VALID_EMAIL);

        OfferDBEntity offerDBEntity = new OfferDBEntity();
        offerDBEntity.setStartDate(ValidateOfferForCreateInputData.TEST_INVALID_DATE_BEFORE_NOW);
        offerDBEntity.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE);
        offerDBEntity.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        offerDBEntity.setMaxOfferRedemptions(ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION);
        offerDBEntity.setActualOfferRedemptions(ValidateOfferForCreateInputData.TEST_ACTUAL_REDEMTION);
        offerDBEntity.setStatus(StatusType.ACTIVE);
        offerDBEntity.setId(1L);
        offerDBEntity.setIsExpirable(false);

        OfferRedeemDBEntity offerRedeemDBEntity = new OfferRedeemDBEntity();
        offerRedeemDBEntity.setEmail(ValidateOfferForCreateInputData.TEST_VALID_EMAIL);
        offerRedeemDBEntity.setIdOffer(ValidateOfferForCreateInputData.TEST_OFFER_ID_REDEEMED);
        offerRedeemDBEntity.setUpdatedOn(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE);

        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE), eq(StatusType.ACTIVE))).thenReturn(offerDBEntity);
        Mockito.when(offerRepository.save(any(OfferDBEntity.class))).thenReturn(offerDBEntity);
        Mockito.when(offerRedeemRepository.save(any(OfferRedeemDBEntity.class))).thenReturn(offerRedeemDBEntity);
        Mockito.when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(new Date(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE));

        HttpEntity<OfferApplyDTO> request = new HttpEntity<>(offerApplyDTO, headers);

        ResponseEntity<OfferApplyDTO> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.APPLY_TO_OFFER),
                HttpMethod.POST, request, OfferApplyDTO.class);

        assertEquals("OK status code was not returned", HttpStatus.OK, response.getStatusCode());
        assertEquals("Offer was successfully applied for offer code", offerApplyDTO.getOfferCode(), response.getBody().getOfferCode());
        assertEquals("Offer was successfully applied to email", offerApplyDTO.getEmail(), response.getBody().getEmail());
        assertEquals("Offer was successfully applied on current date", ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE, response.getBody().getUpdatedOn());

        Mockito.verify(offerRepository).findOneByOfferCodeIgnoreCaseAndStatus(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE), eq(StatusType.ACTIVE));
        Mockito.verify(offerRepository).save(any(OfferDBEntity.class));
        Mockito.verify(offerRedeemRepository).save(any(OfferRedeemDBEntity.class));
        Mockito.verify(jdbcTemplate, times(2)).queryForObject(any(String.class), any(RowMapper.class));
    }

    private void processInvalidOfferValidation(OfferVerifyDTO offerToVerify) throws IOException {
        HttpEntity<OfferVerifyDTO> entity = new HttpEntity<>(offerToVerify, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.VERIFY_OFFER),
                HttpMethod.POST, entity, String.class);

        assertEquals("Bad request status code was not returned", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorMessageDTO messageDTO = objectMapper.reader().forType(ErrorMessageDTO.class).readValue(response.getBody());

        assertEquals("Offer invalid error code missed in response", CodeKeys.OFFER_INVALID, messageDTO.getCode());
        assertEquals("Offer invalid error message missed in response", ValidateOfferForCreateViolationConstraintMessages.OFFER_INVALID, messageDTO.getMessage());
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
        assertEquals("Input value for Value is valid", ValidateOfferForCreateInputData.TEST_VALID_MAX_VALUE, offerDTO.getValue());
        assertEquals("Input value for Offer Redemption is valid", ValidateOfferForCreateInputData.TEST_VALID_MAX_REDEMPTION, offerDTO.getMaxOfferRedemptions());
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
