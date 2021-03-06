package com.ovoenergy.offer.validation.validator;

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
public class FutureDateValidatorTest {

    private static final Long TEST_INVALID_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    private static final Long TEST_VALID_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    @Mock
    private ConstraintValidatorContext mockConstraintValidatorContext;

    @InjectMocks
    private FutureDateValidator unit;

    @Test
    public void testIsValidFailed() {
        Boolean result = unit.isValid(TEST_INVALID_DATE, mockConstraintValidatorContext);
        assertFalse(result);
    }

    @Test
    public void testIsValidSuccessorNull() {
        Boolean result = unit.isValid(null, mockConstraintValidatorContext);
        assertTrue(result);
    }

    @Test
    public void testIsValidSuccess() {
        Boolean result = unit.isValid(TEST_VALID_DATE, mockConstraintValidatorContext);
        assertTrue(result);
    }
}
