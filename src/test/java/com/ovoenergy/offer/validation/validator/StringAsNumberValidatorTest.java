package com.ovoenergy.offer.validation.validator;

import com.google.common.collect.ImmutableMap;
import com.ovoenergy.offer.validation.key.CodeKeys;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringAsNumberValidatorTest extends AbstractConstraintValidatorTest {

    private StringAsNumberValidator stringAsNumberValidator = new StringAsNumberValidator();

    @Before
    public void setUp() {
        StringAsNumberConstraint stringAsNumberConstraint = AbstractConstraintValidatorTest.annotation(
                StringAsNumberConstraint.class,
                ImmutableMap.of("min", 0L, "max", 999L, "maxMessage", "message", "message", "message")
        );
        stringAsNumberValidator.initialize(stringAsNumberConstraint);
    }

    @Test
    public void testIsEmptyString() {
        boolean valid = stringAsNumberValidator.isValid("", context);

        assertThat(valid, is(false));
        assertThat(messageCaptor.getValue(), is(CodeKeys.FIELD_REQUIRED));
    }

    @Test
    public void testIsNotNumberString() {
        boolean valid = stringAsNumberValidator.isValid("abc", context);

        assertThat(valid, is(false));
        assertThat(messageCaptor.getValue(), is("message"));
    }

    @Test
    public void testLessThanMinValue() {
        boolean valid = stringAsNumberValidator.isValid("-1", context);

        assertThat(valid, is(false));
        assertThat(messageCaptor.getValue(), is("message"));
    }

    @Test
    public void testGreaterThanMaxValue() {
        boolean valid = stringAsNumberValidator.isValid("1000", context);

        assertThat(valid, is(false));
        assertThat(messageCaptor.getValue(), is("message"));
    }

    @Test
    public void testValidString() {
        boolean valid = stringAsNumberValidator.isValid("100", context);

        assertThat(valid, is(true));
    }
}