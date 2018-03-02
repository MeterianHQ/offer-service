package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.dto.OfferDTO;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DateFieldsValueValidatorTest extends AbstractConstraintValidatorTest {

    private static final Long TEST_START_DATE_BEFORE_EXPIRY = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    private static final Long TEST_EXPIRY_DATE_AFTER_START = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(2).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();


    @InjectMocks
    private DateFieldsValueValidator unit;

    @Test
    public void testIsValidFailed() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(true);
        offerDTO.setStartDate(TEST_EXPIRY_DATE_AFTER_START);
        offerDTO.setExpiryDate(TEST_START_DATE_BEFORE_EXPIRY);
        boolean result = unit.isValid(offerDTO, context);

        assertThat(result, is(false));
        assertThat(propertyNodeCaptor.getValue(), is("expiryDate"));
    }

    @Test
    public void testIsValidSuccessorExpiryNull() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(true);
        offerDTO.setStartDate(TEST_START_DATE_BEFORE_EXPIRY);
        offerDTO.setExpiryDate(null);
        boolean result = unit.isValid(offerDTO, context);

        assertThat(result, is(false));
        assertThat(propertyNodeCaptor.getValue(), is("expiryDate"));
    }

    @Test
    public void testIsValidSuccess() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setIsExpirable(true);
        offerDTO.setStartDate(TEST_START_DATE_BEFORE_EXPIRY);
        offerDTO.setExpiryDate(TEST_EXPIRY_DATE_AFTER_START);
        boolean result = unit.isValid(offerDTO, context);

        assertThat(result, is(true));
    }

}
