package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.dto.OfferDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DateFieldsValueValidatorTest {

    private static final Long TEST_START_DATE_BEFORE_EXPIRY = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    private static final Long TEST_EXPIRY_DATE_AFTER_START = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(2).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    @Mock
    private ConstraintValidatorContext mockConstraintValidatorContext;

    @InjectMocks
    private DateFieldsValueValidator unit;

    @Test
    public void testIsValidFailed() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(true);
        offerDTO.setStartDate(TEST_EXPIRY_DATE_AFTER_START);
        offerDTO.setExpiryDate(TEST_START_DATE_BEFORE_EXPIRY);
        Boolean result = unit.isValid(offerDTO, mockConstraintValidatorContext);
        assertFalse(result);
    }

    @Test
    public void testIsValidSuccessorExpiryNull() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(true);
        offerDTO.setStartDate(TEST_START_DATE_BEFORE_EXPIRY);
        offerDTO.setExpiryDate(null);
        Boolean result = unit.isValid(offerDTO, mockConstraintValidatorContext);
        assertFalse(result);
    }

    @Test
    public void testIsValidSuccess() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(true);
        offerDTO.setStartDate(TEST_START_DATE_BEFORE_EXPIRY);
        offerDTO.setExpiryDate(TEST_EXPIRY_DATE_AFTER_START);
        Boolean result = unit.isValid(offerDTO, mockConstraintValidatorContext);
        assertTrue(result);
    }

}
