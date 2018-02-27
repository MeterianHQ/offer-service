package com.ovoenergy.offer.integration.offer.management.apply;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferVerifyDTO;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OfferRepositoryTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        String TEST_VALID_CODE = "validCODE";
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
        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_NON_EXISTING_CODE))).thenReturn(null);
        processInvalidOfferValidation(offerToVerify);

        // test non started offer code shouldn't pass validation
        OfferDBEntity offerDBEntityNotStarted = new OfferDBEntity();
        offerDBEntityNotStarted.setStartDate(ValidateOfferForCreateInputData.TEST_VALID_DATE_IN_FUTURE);
        offerDBEntityNotStarted.setExpiryDate(ValidateOfferForCreateInputData.TEST_VALID_EXPIRY_DATE);
        offerDBEntityNotStarted.setStatus(StatusType.ACTIVE);
        offerToVerify.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE))).thenReturn(offerDBEntityNotStarted);
        processInvalidOfferValidation(offerToVerify);
        Mockito.verify(offerRepository).findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE));

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

        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE))).thenReturn(offerDBEntity);

        HttpEntity<OfferVerifyDTO> request = new HttpEntity<>(offerToVerify);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.VERIFY_OFFER),
                HttpMethod.POST, request, String.class);

        assertEquals("Bad request status code was not returned", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorMessageDTO messageDTO = objectMapper.reader().forType(ErrorMessageDTO.class).readValue(response.getBody());

        assertEquals("Offer expired error code missed in response", CodeKeys.OFFER_EXPIRED, messageDTO.getCode());
        assertEquals("Offer expired error message missed in response", ValidateOfferForCreateViolationConstraintMessages.OFFER_EXPIRED, messageDTO.getMessage());
        Mockito.verify(offerRepository, Mockito.times(2)).findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE));
    }

    @Test
    public void verifyOfferSuccessCase() {
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
        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE))).thenReturn(offerDBEntity);

        HttpEntity<OfferVerifyDTO> request = new HttpEntity<>(offerToCreate);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.VERIFY_OFFER),
                HttpMethod.POST, request, Boolean.class);

        assertEquals("OK status code was not returned", HttpStatus.OK, response.getStatusCode());

        assertTrue("Offer was successfully verified", response.getBody());

        Mockito.verify(offerRepository).findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE));
        Mockito.when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(new Date(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE));
    }

    @Test
    public void applyToOfferValidationCases() throws Exception {
        //test invalid format of email
        OfferApplyDTO offerToApply = new OfferApplyDTO();
        offerToApply.setOfferCode(ValidateOfferForCreateInputData.TEST_VALID_CODE);
        HttpEntity<OfferApplyDTO> request = new HttpEntity<>(offerToApply);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.APPLY_TO_OFFER),
                HttpMethod.POST, request, String.class);

        assertEquals("Bad request status code was not returned", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorMessageDTO messageDTO = objectMapper.reader().forType(ErrorMessageDTO.class).readValue(response.getBody());

        assertEquals("Email format is not valid", CodeKeys.INVALID_EMAIL, messageDTO.getCode());
        assertEquals("Offer expired error message missed in response", ValidateOfferForCreateViolationConstraintMessages.INVALID_EMAIL, messageDTO.getMessage());
    }

    @Test
    public void applyToOfferSuccessCase() {
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
        offerRedeemDBEntity.setOfferDBEntity(OfferDBEntity.builder().id(ValidateOfferForCreateInputData.TEST_OFFER_ID_REDEEMED).build());
        offerRedeemDBEntity.setUpdatedOn(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE);

        Mockito.when(offerRepository.findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE))).thenReturn(offerDBEntity);
        Mockito.when(offerRepository.save(any(OfferDBEntity.class))).thenReturn(offerDBEntity);
        Mockito.when(offerRedeemRepository.save(any(OfferRedeemDBEntity.class))).thenReturn(offerRedeemDBEntity);
        Mockito.when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(new Date(ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE));

        HttpEntity<OfferApplyDTO> request = new HttpEntity<>(offerApplyDTO);

        ResponseEntity<OfferApplyDTO> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.APPLY_TO_OFFER),
                HttpMethod.POST, request, OfferApplyDTO.class);

        assertEquals("OK status code was not returned", HttpStatus.OK, response.getStatusCode());
        assertEquals("Offer was successfully applied for offer code", offerApplyDTO.getOfferCode(), response.getBody().getOfferCode());
        assertEquals("Offer was successfully applied to email", offerApplyDTO.getEmail(), response.getBody().getEmail());
        assertEquals("Offer was successfully applied on current date", ValidateOfferForCreateInputData.TEST_VALID_UPDATE_ON_DATE, response.getBody().getUpdatedOn());

        Mockito.verify(offerRepository).findOneByOfferCodeIgnoreCase(eq(ValidateOfferForCreateInputData.TEST_VALID_CODE));
        Mockito.verify(offerRepository).save(any(OfferDBEntity.class));
        Mockito.verify(offerRedeemRepository).save(any(OfferRedeemDBEntity.class));
        Mockito.verify(jdbcTemplate, times(2)).queryForObject(any(String.class), any(RowMapper.class));
    }

    private void processInvalidOfferValidation(OfferVerifyDTO offerToVerify) throws IOException {
        HttpEntity<OfferVerifyDTO> entity = new HttpEntity<>(offerToVerify);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.VERIFY_OFFER),
                HttpMethod.POST, entity, String.class);

        assertEquals("Bad request status code was not returned", HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorMessageDTO messageDTO = objectMapper.reader().forType(ErrorMessageDTO.class).readValue(response.getBody());

        assertEquals("Offer invalid error code missed in response", CodeKeys.OFFER_INVALID, messageDTO.getCode());
        assertEquals("Offer invalid error message missed in response", ValidateOfferForCreateViolationConstraintMessages.OFFER_INVALID, messageDTO.getMessage());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
