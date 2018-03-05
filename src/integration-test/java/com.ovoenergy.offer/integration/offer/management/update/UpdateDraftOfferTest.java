package com.ovoenergy.offer.integration.offer.management.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.integration.data.TestData;
import com.ovoenergy.offer.integration.mock.config.OfferRepositoryTestConfiguration;
import com.ovoenergy.offer.validation.key.CodeKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.ovoenergy.offer.dto.OffersServiceURLs.UPDATE_OFFER;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.only;
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
public class UpdateDraftOfferTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        Long id = 100L;
        OfferDTO offerToValidate = TestData.prepareForValidOfferDTO(StatusType.DRAFT);
        offerToValidate.setId(id);

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
                .andExpect(jsonPath("$.value", equalTo(offerToValidate.getValue())))
                .andExpect(jsonPath("$.maxOfferRedemptions", equalTo(offerToValidate.getMaxOfferRedemptions())))
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

    @Test
    public void testInvalidErrorCode() throws Exception {
        Long id = 100L;
        OfferDTO offerToValidate = TestData.prepareForValidOfferDTO(StatusType.DRAFT);
        offerToValidate.setId(id);

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
                .andExpect(jsonPath("$.value", equalTo(offerToValidate.getValue())))
                .andExpect(jsonPath("$.maxOfferRedemptions", equalTo(offerToValidate.getMaxOfferRedemptions())))
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

    @Test
    public void testExpireDateGreaterThanStartDateError() throws Exception {
        Long testInvalidExpiryDate = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .minusDays(1)
                .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        Long id = 100L;
        OfferDTO offerToValidate = TestData.prepareForValidOfferDTO(StatusType.DRAFT);
        offerToValidate.setId(id);
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
                .andExpect(jsonPath("$.value", equalTo(offerToValidate.getValue())))
                .andExpect(jsonPath("$.maxOfferRedemptions", equalTo(offerToValidate.getMaxOfferRedemptions())))
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

    @Test
    public void testInvalidExpireFlowError() throws Exception {
        Long id = 100L;
        OfferDTO offerToValidate = TestData.prepareForValidOfferDTO(StatusType.DRAFT);
        offerToValidate.setId(id);
        offerToValidate.setIsExpirable(false);

        when(offerRepository.exists(anyLong())).thenReturn(true);
        when(offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong())).thenReturn(false);

        mvc.perform(put(UPDATE_OFFER, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerToValidate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(jsonPath("$.constraintViolations.expiryDate", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.expiryDate[0].code", equalTo(CodeKeys.NO_EXPIRY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE)))
                .andExpect(jsonPath("$.constraintViolations.expiryDate[0].message", equalTo("No expiry date' cannot be ticked if 'Expiry date' selected")))
                .andExpect(jsonPath("$.id", equalTo(offerToValidate.getId().intValue())))
                .andExpect(jsonPath("$.offerCode", equalTo(offerToValidate.getOfferCode())))
                .andExpect(jsonPath("$.offerName", equalTo(offerToValidate.getOfferName())))
                .andExpect(jsonPath("$.description", equalTo(offerToValidate.getDescription())))
                .andExpect(jsonPath("$.supplier", equalTo(offerToValidate.getSupplier())))
                .andExpect(jsonPath("$.offerType", equalTo(offerToValidate.getOfferType())))
                .andExpect(jsonPath("$.value", equalTo(offerToValidate.getValue())))
                .andExpect(jsonPath("$.maxOfferRedemptions", equalTo(offerToValidate.getMaxOfferRedemptions())))
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

    @Test
    public void testInvalidRequestError() throws Exception {
        Long id = 100L;
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setId(id);
        offerToValidate.setIsExpirable(false);

        when(offerRepository.exists(anyLong())).thenReturn(true);
        when(offerRepository.findOne(anyLong())).thenReturn(null);
        when(offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong())).thenReturn(false);

        mvc.perform(put(UPDATE_OFFER, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerToValidate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(jsonPath("$.constraintViolations.offerType", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.offerType[0].code", equalTo(CodeKeys.NOT_NULL_FIELD)))
                .andExpect(jsonPath("$.constraintViolations.offerType[0].message", equalTo("This field cannot be null")))
                .andExpect(jsonPath("$.constraintViolations.offerName", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.offerName[0].code", equalTo(CodeKeys.FIELD_REQUIRED)))
                .andExpect(jsonPath("$.constraintViolations.offerName[0].message", equalTo("This field is required")))
                .andExpect(jsonPath("$.constraintViolations.maxOfferRedemptions", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.maxOfferRedemptions[0].code", equalTo(CodeKeys.FIELD_REQUIRED)))
                .andExpect(jsonPath("$.constraintViolations.maxOfferRedemptions[0].message", equalTo("This field is required")))
                .andExpect(jsonPath("$.constraintViolations.offerCode", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.offerCode[0].code", equalTo(CodeKeys.FIELD_REQUIRED)))
                .andExpect(jsonPath("$.constraintViolations.offerCode[0].message", equalTo("This field is required")))
                .andExpect(jsonPath("$.constraintViolations.supplier", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.supplier[0].code", equalTo(CodeKeys.NOT_NULL_FIELD)))
                .andExpect(jsonPath("$.constraintViolations.supplier[0].message", equalTo("This field cannot be null")))
                .andExpect(jsonPath("$.constraintViolations.channel", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.channel[0].code", equalTo(CodeKeys.NOT_NULL_FIELD)))
                .andExpect(jsonPath("$.constraintViolations.channel[0].message", equalTo("This field cannot be null")))
                .andExpect(jsonPath("$.constraintViolations.eligibilityCriteria", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.eligibilityCriteria[0].code", equalTo(CodeKeys.NOT_NULL_FIELD)))
                .andExpect(jsonPath("$.constraintViolations.eligibilityCriteria[0].message", equalTo("This field cannot be null")))
                .andExpect(jsonPath("$.constraintViolations.value", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.value[0].code", equalTo(CodeKeys.FIELD_REQUIRED)))
                .andExpect(jsonPath("$.constraintViolations.value[0].message", equalTo("This field is required")))
                .andExpect(jsonPath("$.constraintViolations.startDate", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.startDate[0].code", equalTo(CodeKeys.NOT_NULL_FIELD)))
                .andExpect(jsonPath("$.constraintViolations.startDate[0].message", equalTo("This field cannot be null")))
                .andExpect(jsonPath("$.constraintViolations.status", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.status[0].code", equalTo(CodeKeys.NOT_NULL_FIELD)))
                .andExpect(jsonPath("$.constraintViolations.status[0].message", equalTo("This field cannot be null")))
                .andExpect(jsonPath("$.id", equalTo(offerToValidate.getId().intValue())))
                .andExpect(jsonPath("$.offerCode", equalTo(offerToValidate.getOfferCode())))
                .andExpect(jsonPath("$.offerName", equalTo(offerToValidate.getOfferName())))
                .andExpect(jsonPath("$.description", equalTo(offerToValidate.getDescription())))
                .andExpect(jsonPath("$.supplier", equalTo(offerToValidate.getSupplier())))
                .andExpect(jsonPath("$.offerType", equalTo(offerToValidate.getOfferType())))
                .andExpect(jsonPath("$.value", nullValue()))
                .andExpect(jsonPath("$.maxOfferRedemptions", nullValue()))
                .andExpect(jsonPath("$.actualOfferRedemptions", nullValue()))
                .andExpect(jsonPath("$.startDate", nullValue()))
                .andExpect(jsonPath("$.expiryDate", nullValue()))
                .andExpect(jsonPath("$.isExpirable", equalTo(offerToValidate.getIsExpirable())))
                .andExpect(jsonPath("$.eligibilityCriteria", equalTo(offerToValidate.getEligibilityCriteria())))
                .andExpect(jsonPath("$.channel", equalTo(offerToValidate.getChannel())))
                .andExpect(jsonPath("$.status", equalTo(offerToValidate.getStatus())))
                .andExpect(jsonPath("$.updatedOn", equalTo(offerToValidate.getUpdatedOn())));

        verify(offerRepository, times(1)).exists(eq(id));
        verify(offerRepository, times(1)).existsByOfferCodeIgnoreCaseAndIdIsNot(eq(offerToValidate.getOfferCode()), eq(offerToValidate.getId()));
        verify(offerRepository, times(1)).findOne(eq(offerToValidate.getId()));
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testEmptyFieldsValidations() throws Exception {
        Long id = 100L;
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setId(id);
        offerToValidate.setIsExpirable(false);
        offerToValidate.setOfferName("");
        offerToValidate.setOfferCode("");
        offerToValidate.setSupplier("");
        offerToValidate.setOfferType("");
        offerToValidate.setValue(null);
        offerToValidate.setMaxOfferRedemptions(null);
        offerToValidate.setStartDate(null);
        offerToValidate.setExpiryDate(null);

        offerToValidate.setEligibilityCriteria("");
        offerToValidate.setChannel("");
        offerToValidate.setStatus("");



        when(offerRepository.exists(anyLong())).thenReturn(true);
        when(offerRepository.findOne(anyLong())).thenReturn(null);
        when(offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong())).thenReturn(false);

        mvc.perform(put(UPDATE_OFFER, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerToValidate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(jsonPath("$.constraintViolations.offerType", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.offerType[0].code", equalTo(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)))
                .andExpect(jsonPath("$.constraintViolations.offerType[0].message", equalTo("Provided value is not supported")))
                .andExpect(jsonPath("$.constraintViolations.offerName", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.offerName[0].code", equalTo(CodeKeys.FIELD_REQUIRED)))
                .andExpect(jsonPath("$.constraintViolations.offerName[0].message", equalTo("This field is required")))
                .andExpect(jsonPath("$.constraintViolations.maxOfferRedemptions", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.maxOfferRedemptions[0].code", equalTo(CodeKeys.FIELD_REQUIRED)))
                .andExpect(jsonPath("$.constraintViolations.maxOfferRedemptions[0].message", equalTo("This field is required")))
                .andExpect(jsonPath("$.constraintViolations.offerCode", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.offerCode[0].code", equalTo(CodeKeys.FIELD_REQUIRED)))
                .andExpect(jsonPath("$.constraintViolations.offerCode[0].message", equalTo("This field is required")))
                .andExpect(jsonPath("$.constraintViolations.supplier", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.supplier[0].code", equalTo(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)))
                .andExpect(jsonPath("$.constraintViolations.supplier[0].message", equalTo("Provided value is not supported")))
                .andExpect(jsonPath("$.constraintViolations.channel", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.channel[0].code", equalTo(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)))
                .andExpect(jsonPath("$.constraintViolations.channel[0].message", equalTo("Provided value is not supported")))
                .andExpect(jsonPath("$.constraintViolations.eligibilityCriteria", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.eligibilityCriteria[0].code", equalTo(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)))
                .andExpect(jsonPath("$.constraintViolations.eligibilityCriteria[0].message", equalTo("Provided value is not supported")))
                .andExpect(jsonPath("$.constraintViolations.value", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.value[0].code", equalTo(CodeKeys.FIELD_REQUIRED)))
                .andExpect(jsonPath("$.constraintViolations.value[0].message", equalTo("This field is required")))
                .andExpect(jsonPath("$.constraintViolations.startDate", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.startDate[0].code", equalTo(CodeKeys.NOT_NULL_FIELD)))
                .andExpect(jsonPath("$.constraintViolations.startDate[0].message", equalTo("This field cannot be null")))

                .andExpect(jsonPath("$.constraintViolations.status", hasSize(1)))
                .andExpect(jsonPath("$.constraintViolations.status[0].code", equalTo(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)))
                .andExpect(jsonPath("$.constraintViolations.status[0].message", equalTo("Provided value is not supported")))


                .andExpect(jsonPath("$.id", equalTo(offerToValidate.getId().intValue())))
                .andExpect(jsonPath("$.offerCode", equalTo(offerToValidate.getOfferCode())))
                .andExpect(jsonPath("$.offerName", equalTo(offerToValidate.getOfferName())))
                .andExpect(jsonPath("$.description", equalTo(offerToValidate.getDescription())))
                .andExpect(jsonPath("$.supplier", equalTo(offerToValidate.getSupplier())))
                .andExpect(jsonPath("$.offerType", equalTo(offerToValidate.getOfferType())))
                .andExpect(jsonPath("$.value", nullValue()))
                .andExpect(jsonPath("$.maxOfferRedemptions", nullValue()))
                .andExpect(jsonPath("$.actualOfferRedemptions", nullValue()))
                .andExpect(jsonPath("$.startDate", nullValue()))
                .andExpect(jsonPath("$.expiryDate", nullValue()))
                .andExpect(jsonPath("$.isExpirable", equalTo(offerToValidate.getIsExpirable())))
                .andExpect(jsonPath("$.eligibilityCriteria", equalTo(offerToValidate.getEligibilityCriteria())))
                .andExpect(jsonPath("$.channel", equalTo(offerToValidate.getChannel())))
                .andExpect(jsonPath("$.status", equalTo(offerToValidate.getStatus())))
                .andExpect(jsonPath("$.updatedOn", equalTo(offerToValidate.getUpdatedOn())));

        verify(offerRepository, times(1)).exists(eq(id));
        verify(offerRepository, times(1)).existsByOfferCodeIgnoreCaseAndIdIsNot(eq(offerToValidate.getOfferCode()), eq(offerToValidate.getId()));
        verify(offerRepository, times(1)).findOne(eq(offerToValidate.getId()));
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testUpdateValidOffer() throws Exception {
        Long id = 10L;
        OfferDTO offerToValidate = TestData.prepareForValidOfferDTO(StatusType.DRAFT);
        OfferDBEntity offerDBEntity = TestData.prepareForTestValidOfferDBEntity(StatusType.DRAFT);

        offerToValidate.setId(id);
        offerDBEntity.setId(id);

        when(offerRepository.save(any(OfferDBEntity.class))).then(AdditionalAnswers.returnsFirstArg());
        when(offerRepository.findOne(anyLong())).thenReturn(offerDBEntity);
        when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(new Date(LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()));
        when(offerRepository.exists(anyLong())).thenReturn(true);
        when(offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong())).thenReturn(false);

        mvc.perform(put(UPDATE_OFFER, offerToValidate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(jsonPath("$.id", equalTo(offerToValidate.getId().intValue())))
                .andExpect(jsonPath("$.offerCode", equalTo(offerToValidate.getOfferCode())))
                .andExpect(jsonPath("$.offerName", equalTo(offerToValidate.getOfferName())))
                .andExpect(jsonPath("$.description", equalTo(offerToValidate.getDescription())))
                .andExpect(jsonPath("$.supplier", equalTo(offerToValidate.getSupplier())))
                .andExpect(jsonPath("$.offerType", equalTo(offerToValidate.getOfferType())))
                .andExpect(jsonPath("$.value", equalTo(offerToValidate.getValue())))
                .andExpect(jsonPath("$.maxOfferRedemptions", equalTo(offerToValidate.getMaxOfferRedemptions())))
                .andExpect(jsonPath("$.actualOfferRedemptions", nullValue()))
                .andExpect(jsonPath("$.startDate", comparesEqualTo(offerToValidate.getStartDate())))
                .andExpect(jsonPath("$.expiryDate", comparesEqualTo(offerToValidate.getExpiryDate())))
                .andExpect(jsonPath("$.isExpirable", equalTo(offerToValidate.getIsExpirable())))
                .andExpect(jsonPath("$.eligibilityCriteria", equalTo(offerToValidate.getEligibilityCriteria())))
                .andExpect(jsonPath("$.channel", equalTo(offerToValidate.getChannel())))
                .andExpect(jsonPath("$.status", equalTo(offerToValidate.getStatus())))
                .andExpect(jsonPath("$.updatedOn", greaterThanOrEqualTo(offerDBEntity.getUpdatedOn())));

        verify(offerRepository, times(1)).exists(eq(id));
        verify(offerRepository, times(1)).existsByOfferCodeIgnoreCaseAndIdIsNot(eq(offerToValidate.getOfferCode()), eq(offerToValidate.getId()));
        verify(offerRepository, times(1)).save(any(OfferDBEntity.class));
        verify(offerRepository, times(1)).findOne(anyLong());
        verify(jdbcTemplate, only()).queryForObject(any(String.class), any(RowMapper.class));
        verifyNoMoreInteractions(offerRepository, jdbcTemplate);
    }
}
