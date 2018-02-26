package com.ovoenergy.offer.integration.offer.management.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.integration.mock.config.OfferRepositoryTestConfiguration;
import com.ovoenergy.offer.validation.key.CodeKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.ovoenergy.offer.dto.OffersServiceURLs.UPDATE_OFFER;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OfferRepositoryTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditDraftOfferTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OfferRepository offerRepository;

    @Test
    public void testDifferentIdInRequestBodyAndPathVariableError() throws Exception {
        Long pathId = 100L;
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setId(1L);

        mvc.perform(put(UPDATE_OFFER, pathId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerToValidate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(jsonPath("$.code", equalTo(CodeKeys.PROVIDED_TWO_DIFFERENT_IDS)))
                .andExpect(jsonPath("$.message", equalTo("Provided ids are different")));
    }

    @Test
    public void testEntityNotExistsError() throws Exception {
        Long testValidDateInFuture = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        Long id = 100L;
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setId(id);
        offerToValidate.setIsExpirable(false);
        offerToValidate.setOfferCode("validCODE");
        offerToValidate.setOfferName("validName");
        offerToValidate.setMaxOfferRedemptions(10L);
        offerToValidate.setSupplier("Amazon");
        offerToValidate.setChannel("Email");
        offerToValidate.setEligibilityCriteria("SSD");
        offerToValidate.setOfferType("Giftcard");
        offerToValidate.setValue(10L);
        offerToValidate.setStartDate(testValidDateInFuture);
        offerToValidate.setStatus("draft");

        when(offerRepository.exists(anyLong())).thenReturn(false);

        mvc.perform(put(UPDATE_OFFER, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerToValidate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(jsonPath("$.constraintViolations.id", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.id[0].code", equalTo(CodeKeys.ENTITY_NOT_EXIST)))
                .andExpect(jsonPath("$.constraintViolations.id[0].message", equalTo("Entity doesn't exist")))
                .andExpect(jsonPath("$.id", equalTo(offerToValidate.getId().intValue())))
                .andExpect(jsonPath("$.offerCode", equalTo(offerToValidate.getOfferCode())))
                .andExpect(jsonPath("$.offerName", equalTo(offerToValidate.getOfferName())))
                .andExpect(jsonPath("$.description", equalTo(offerToValidate.getDescription())))
                .andExpect(jsonPath("$.supplier", equalTo(offerToValidate.getSupplier())))
                .andExpect(jsonPath("$.offerType", equalTo(offerToValidate.getOfferType())))
                .andExpect(jsonPath("$.value", equalTo(offerToValidate.getValue().intValue())))
                .andExpect(jsonPath("$.maxOfferRedemptions", equalTo(offerToValidate.getMaxOfferRedemptions().intValue())))
                .andExpect(jsonPath("$.actualOfferRedemptions", nullValue()))
                .andExpect(jsonPath("$.startDate", comparesEqualTo(offerToValidate.getStartDate())))
                .andExpect(jsonPath("$.expiryDate", nullValue()))
                .andExpect(jsonPath("$.isExpirable", equalTo(offerToValidate.getIsExpirable())))
                .andExpect(jsonPath("$.eligibilityCriteria", equalTo(offerToValidate.getEligibilityCriteria())))
                .andExpect(jsonPath("$.channel", equalTo(offerToValidate.getChannel())))
                .andExpect(jsonPath("$.status", equalTo(offerToValidate.getStatus())))
                .andExpect(jsonPath("$.updatedOn", equalTo(offerToValidate.getUpdatedOn())));

        verify(offerRepository, times(1)).exists(eq(id));
        verify(offerRepository, times(1)).existsByOfferCodeIgnoreCaseAndIdIsNot(eq(offerToValidate.getOfferCode()), eq(offerToValidate.getId()));
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testInvalidErrorCode() throws Exception {
        Long testValidDateInFuture = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        Long id = 100L;
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setId(id);
        offerToValidate.setIsExpirable(false);
        offerToValidate.setOfferCode("validCODE");
        offerToValidate.setOfferName("validName");
        offerToValidate.setMaxOfferRedemptions(10L);
        offerToValidate.setSupplier("Amazon");
        offerToValidate.setChannel("Email");
        offerToValidate.setEligibilityCriteria("SSD");
        offerToValidate.setOfferType("Giftcard");
        offerToValidate.setValue(10L);
        offerToValidate.setStartDate(testValidDateInFuture);
        offerToValidate.setStatus("draft");

        when(offerRepository.exists(anyLong())).thenReturn(true);
        when(offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong())).thenReturn(true);

        mvc.perform(put(UPDATE_OFFER, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerToValidate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(jsonPath("$.constraintViolations.offerCode", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.offerCode[0].code", equalTo(CodeKeys.NOT_UNIQUE_OFFER_CODE)))
                .andExpect(jsonPath("$.constraintViolations.offerCode[0].message", equalTo("Please choose a unique offer code")))
                .andExpect(jsonPath("$.id", equalTo(offerToValidate.getId().intValue())))
                .andExpect(jsonPath("$.offerCode", equalTo(offerToValidate.getOfferCode())))
                .andExpect(jsonPath("$.offerName", equalTo(offerToValidate.getOfferName())))
                .andExpect(jsonPath("$.description", equalTo(offerToValidate.getDescription())))
                .andExpect(jsonPath("$.supplier", equalTo(offerToValidate.getSupplier())))
                .andExpect(jsonPath("$.offerType", equalTo(offerToValidate.getOfferType())))
                .andExpect(jsonPath("$.value", equalTo(offerToValidate.getValue().intValue())))
                .andExpect(jsonPath("$.maxOfferRedemptions", equalTo(offerToValidate.getMaxOfferRedemptions().intValue())))
                .andExpect(jsonPath("$.actualOfferRedemptions", nullValue()))
                .andExpect(jsonPath("$.startDate", comparesEqualTo(offerToValidate.getStartDate())))
                .andExpect(jsonPath("$.expiryDate", nullValue()))
                .andExpect(jsonPath("$.isExpirable", equalTo(offerToValidate.getIsExpirable())))
                .andExpect(jsonPath("$.eligibilityCriteria", equalTo(offerToValidate.getEligibilityCriteria())))
                .andExpect(jsonPath("$.channel", equalTo(offerToValidate.getChannel())))
                .andExpect(jsonPath("$.status", equalTo(offerToValidate.getStatus())))
                .andExpect(jsonPath("$.updatedOn", equalTo(offerToValidate.getUpdatedOn())));

        verify(offerRepository, times(1)).exists(eq(id));
        verify(offerRepository, times(1)).existsByOfferCodeIgnoreCaseAndIdIsNot(eq(offerToValidate.getOfferCode()), eq(offerToValidate.getId()));
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testExpireDateGreaterThanStartDateError() throws Exception {
        Long testValidDateInFuture = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        Long testInvalidExpiryDate = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .minusDays(1)
                .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        Long id = 100L;
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setId(id);
        offerToValidate.setIsExpirable(true);
        offerToValidate.setOfferCode("validCODE");
        offerToValidate.setOfferName("validName");
        offerToValidate.setMaxOfferRedemptions(10L);
        offerToValidate.setSupplier("Amazon");
        offerToValidate.setChannel("Email");
        offerToValidate.setEligibilityCriteria("SSD");
        offerToValidate.setOfferType("Giftcard");
        offerToValidate.setValue(10L);
        offerToValidate.setStartDate(testValidDateInFuture);
        offerToValidate.setExpiryDate(testInvalidExpiryDate);
        offerToValidate.setStatus("draft");

        when(offerRepository.exists(anyLong())).thenReturn(true);
        when(offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong())).thenReturn(false);

        mvc.perform(put(UPDATE_OFFER, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerToValidate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(jsonPath("$.constraintViolations.expiryDate", hasSize(2)))
                .andExpect(jsonPath("$.constraintViolations.expiryDate[0].code", equalTo(CodeKeys.NON_IN_FUTURE_DATE)))
                .andExpect(jsonPath("$.constraintViolations.expiryDate[0].message", equalTo("Please select a date in the future")))
                .andExpect(jsonPath("$.constraintViolations.expiryDate[1].code", equalTo(CodeKeys.OFFER_EXPIRY_DATE_BEFORE_START_DATE)))
                .andExpect(jsonPath("$.constraintViolations.expiryDate[1].message", equalTo("Offer Expiry Date must be after the Offer Start Date")))
                .andExpect(jsonPath("$.id", equalTo(offerToValidate.getId().intValue())))
                .andExpect(jsonPath("$.offerCode", equalTo(offerToValidate.getOfferCode())))
                .andExpect(jsonPath("$.offerName", equalTo(offerToValidate.getOfferName())))
                .andExpect(jsonPath("$.description", equalTo(offerToValidate.getDescription())))
                .andExpect(jsonPath("$.supplier", equalTo(offerToValidate.getSupplier())))
                .andExpect(jsonPath("$.offerType", equalTo(offerToValidate.getOfferType())))
                .andExpect(jsonPath("$.value", equalTo(offerToValidate.getValue().intValue())))
                .andExpect(jsonPath("$.maxOfferRedemptions", equalTo(offerToValidate.getMaxOfferRedemptions().intValue())))
                .andExpect(jsonPath("$.actualOfferRedemptions", nullValue()))
                .andExpect(jsonPath("$.startDate", comparesEqualTo(offerToValidate.getStartDate())))
                .andExpect(jsonPath("$.expiryDate", comparesEqualTo(offerToValidate.getExpiryDate())))
                .andExpect(jsonPath("$.isExpirable", equalTo(offerToValidate.getIsExpirable())))
                .andExpect(jsonPath("$.eligibilityCriteria", equalTo(offerToValidate.getEligibilityCriteria())))
                .andExpect(jsonPath("$.channel", equalTo(offerToValidate.getChannel())))
                .andExpect(jsonPath("$.status", equalTo(offerToValidate.getStatus())))
                .andExpect(jsonPath("$.updatedOn", equalTo(offerToValidate.getUpdatedOn())));

        verify(offerRepository, times(1)).exists(eq(id));
        verify(offerRepository, times(1)).existsByOfferCodeIgnoreCaseAndIdIsNot(eq(offerToValidate.getOfferCode()), eq(offerToValidate.getId()));
        verifyNoMoreInteractions(offerRepository);
    }
}
