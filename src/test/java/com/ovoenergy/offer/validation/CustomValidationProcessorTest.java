package com.ovoenergy.offer.validation;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.google.common.collect.Sets;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.test.utils.UnitTest;
import com.ovoenergy.offer.validation.group.BaseOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredActiveOfferChecks;
import com.ovoenergy.offer.validation.key.CodeKeys;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@UnitTest
public class CustomValidationProcessorTest {

    @Mock
    private Validator mockValidator;

    @Mock
    private ConstraintViolation<OfferDTO> mockOfferDTOConstraintViolation;

    @Mock
    private Path mockPropertyPath;

    @Mock
    private MessageSource mockMsgSource;

    @InjectMocks
    private CustomValidationProcessor unit = new CustomValidationProcessor();

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();

    @Fixture
    public OfferDTO fixtureOfferDTO;

    @Test
    public void testProcessValidationSuccess() {
        Set<ConstraintViolation<OfferDTO>> constraintViolations = Sets.newHashSet(mockOfferDTOConstraintViolation);
        when(mockOfferDTOConstraintViolation.getPropertyPath()).thenReturn(mockPropertyPath);
        when(mockOfferDTOConstraintViolation.getMessage()).thenReturn(CodeKeys.FIELD_REQUIRED);
        when(mockPropertyPath.toString()).thenReturn("propertyPath");
        when(mockValidator.validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class), eq(RequiredActiveOfferChecks.class))).thenReturn(constraintViolations);
        when(mockMsgSource.getMessage(any(), any(), any())).thenReturn("Error Message");

        OfferValidationDTO result = unit.processActiveOfferInputDataValidationViolations(fixtureOfferDTO);


        assertEquals(1, result.getConstraintViolations().get("propertyPath").size());
        verify(mockOfferDTOConstraintViolation).getPropertyPath();
        verify(mockOfferDTOConstraintViolation).getMessage();
        verify(mockValidator).validate(eq(fixtureOfferDTO),  eq(BaseOfferChecks.class), eq(RequiredActiveOfferChecks.class));
        verify(mockMsgSource).getMessage(any(), any(), any());
    }
}
