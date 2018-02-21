package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.dto.OfferDTO;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExpiryFieldsValueValidatorTest extends AbstractConstraintValidatorTest {

    private static final Long TEST_EXPIRY_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(2).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    @InjectMocks
    private ExpiryFieldsValueValidator unit;

    @Test
    public void testIsValidFailed() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(false);
        offerDTO.setExpiryDate(TEST_EXPIRY_DATE);
        Boolean result = unit.isValid(offerDTO, context);
        assertFalse(result);
    }

    @Test
    public void testIsValidExpiryNullSuccess() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(false);
        offerDTO.setExpiryDate(null);
        Boolean result = unit.isValid(offerDTO, context);
        assertTrue(result);
    }

    @Test
    public void testIsValidExpiryNullFailed() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(false);
        offerDTO.setExpiryDate(TEST_EXPIRY_DATE);
        Boolean result = unit.isValid(offerDTO, context);
        assertFalse(result);
    }

    @Test
    public void testIsValidSuccess() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(true);
        offerDTO.setExpiryDate(TEST_EXPIRY_DATE);
        Boolean result = unit.isValid(offerDTO, context);
        assertTrue(result);
    }
}
