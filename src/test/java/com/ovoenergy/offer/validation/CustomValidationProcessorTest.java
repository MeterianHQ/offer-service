package com.ovoenergy.offer.validation;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.exception.RequestIdsInValidException;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.validation.group.BaseOfferChecks;
import com.ovoenergy.offer.validation.group.EmptyDraftOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredActiveOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredDraftOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredOfferCreateChecks;
import com.ovoenergy.offer.validation.group.RequiredOfferUpdateChecks;
import com.ovoenergy.offer.validation.key.CodeKeys;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomValidationProcessorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Validator mockValidator;

    @Mock
    private ConstraintViolation<OfferDTO> mockOfferDTOConstraintViolation;

    @Mock
    private MessageSource mockMsgSource;

    @Mock
    private Path mockPropertyPath;

    @InjectMocks
    private CustomValidationProcessor unit;

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();

    @Fixture
    private OfferDTO fixtureOfferDTO;

    @Test
    public void testProcessOfferCreateValidationSuccess() {
        when(mockValidator.validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class),
                eq(RequiredActiveOfferChecks.class), eq(RequiredOfferCreateChecks.class))).thenReturn(Collections.emptySet());

        OfferValidationDTO result = unit.processOfferCreateValidation(fixtureOfferDTO);

        assertThat(result, is(nullValue()));

        verify(mockValidator).validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class),
                eq(RequiredActiveOfferChecks.class), eq(RequiredOfferCreateChecks.class));
    }

    @Test
    public void testProcessOfferCreateValidationError() {
        Set<ConstraintViolation<OfferDTO>> constraintViolations = Collections.singleton(mockOfferDTOConstraintViolation);
        when(mockOfferDTOConstraintViolation.getMessage()).thenReturn(CodeKeys.FIELD_REQUIRED);
        when(mockOfferDTOConstraintViolation.getPropertyPath()).thenReturn(mockPropertyPath);
        when(mockMsgSource.getMessage(any(), any(), any())).thenReturn("Error Message");
        when(mockValidator.validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class),
                eq(RequiredActiveOfferChecks.class), eq(RequiredOfferCreateChecks.class))).thenReturn(constraintViolations);

        OfferValidationDTO result = unit.processOfferCreateValidation(fixtureOfferDTO);

        assertThat(result.getConstraintViolations().get("mockPropertyPath"), hasSize(1));

        verify(mockOfferDTOConstraintViolation).getPropertyPath();
        verify(mockOfferDTOConstraintViolation).getMessage();
        verify(mockMsgSource).getMessage(any(), any(), any());
        verify(mockValidator).validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class),
                eq(RequiredActiveOfferChecks.class), eq(RequiredOfferCreateChecks.class));
    }

    @Test
    public void testProcessOfferCreateValidationSuccessDraft() {
        fixtureOfferDTO.setStatus("draft");
        when(mockValidator.validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class),
                eq(RequiredDraftOfferChecks.class), eq(RequiredOfferCreateChecks.class))).thenReturn(Collections.emptySet());

        OfferValidationDTO result = unit.processOfferCreateValidation(fixtureOfferDTO);

        assertThat(result, is(nullValue()));

        verify(mockValidator, times(1)).validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class),
                eq(RequiredDraftOfferChecks.class), eq(RequiredOfferCreateChecks.class));
    }

    @Test
    public void testProcessOfferCreateValidationErrorDraft() {
        fixtureOfferDTO.setStatus("draft");
        Set<ConstraintViolation<OfferDTO>> constraintViolations = Collections.singleton(mockOfferDTOConstraintViolation);
        when(mockOfferDTOConstraintViolation.getMessage()).thenReturn(CodeKeys.FIELD_REQUIRED);
        when(mockOfferDTOConstraintViolation.getPropertyPath()).thenReturn(mockPropertyPath);
        when(mockMsgSource.getMessage(any(), any(), any())).thenReturn("Error Message");
        when(mockValidator.validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class),
                eq(RequiredDraftOfferChecks.class), eq(RequiredOfferCreateChecks.class))).thenReturn(constraintViolations);

        OfferValidationDTO result = unit.processOfferCreateValidation(fixtureOfferDTO);

        assertThat(result.getConstraintViolations().get("mockPropertyPath"), hasSize(1));

        verify(mockOfferDTOConstraintViolation).getPropertyPath();
        verify(mockOfferDTOConstraintViolation).getMessage();
        verify(mockMsgSource).getMessage(any(), any(), any());
        verify(mockValidator, times(1)).validate(eq(fixtureOfferDTO), eq(BaseOfferChecks.class),
                eq(RequiredDraftOfferChecks.class), eq(RequiredOfferCreateChecks.class));
    }

    @Test
    public void testProcessOfferUpdateValidationSuccess() {
        Long id = 1L;
        fixtureOfferDTO.setId(id);

        when(mockValidator.validate(eq(fixtureOfferDTO), eq(RequiredOfferUpdateChecks.class),
                eq(BaseOfferChecks.class), eq(RequiredActiveOfferChecks.class))).thenReturn(Collections.emptySet());

        OfferValidationDTO result = unit.processOfferUpdateValidation(fixtureOfferDTO, id);

        assertThat(result, is(nullValue()));

        verify(mockValidator, times(1)).validate(eq(fixtureOfferDTO), eq(RequiredOfferUpdateChecks.class),
                eq(BaseOfferChecks.class), eq(RequiredActiveOfferChecks.class));
    }

    @Test
    public void testProcessOfferUpdateValidationSuccessDraft() {
        Long id = 1L;
        fixtureOfferDTO.setId(id);
        fixtureOfferDTO.setStatus("draft");

        when(mockValidator.validate(eq(fixtureOfferDTO), eq(EmptyDraftOfferChecks.class))).thenReturn(Collections.emptySet());
        when(mockValidator.validate(eq(fixtureOfferDTO), eq(RequiredOfferUpdateChecks.class),
                eq(BaseOfferChecks.class), eq(RequiredDraftOfferChecks.class))).thenReturn(Collections.emptySet());

        OfferValidationDTO result = unit.processOfferUpdateValidation(fixtureOfferDTO, id);

        assertThat(result, is(nullValue()));

        verify(mockValidator, times(1)).validate(eq(fixtureOfferDTO), eq(EmptyDraftOfferChecks.class));
        verify(mockValidator, times(1)).validate(eq(fixtureOfferDTO), eq(RequiredOfferUpdateChecks.class),
                eq(BaseOfferChecks.class), eq(RequiredDraftOfferChecks.class));
    }

    @Test
    public void testProcessOfferUpdateValidationDifferentIdError() {
        expectedException.expect(instanceOf(RequestIdsInValidException.class));

        Long id = 10L;
        fixtureOfferDTO.setId(1L);

        unit.processOfferUpdateValidation(fixtureOfferDTO, id);
    }

    @Test
    public void testProcessOfferInputDataInvalidOfferExceptionSuccess() {
        when(mockValidator.validate(eq(fixtureOfferDTO))).thenReturn(Collections.emptySet());

        unit.processOfferInputDataInvalidOfferException(fixtureOfferDTO);

        verify(mockValidator).validate(eq(fixtureOfferDTO));
    }

    @Test
    public void testProcessOfferInputDataInvalidOfferExceptionError() {
        expectedException.expect(instanceOf(VariableNotValidException.class));

        Set<ConstraintViolation<OfferDTO>> constraintViolations = Collections.singleton(mockOfferDTOConstraintViolation);
        when(mockValidator.validate(eq(fixtureOfferDTO))).thenReturn(constraintViolations);

        unit.processOfferInputDataInvalidOfferException(fixtureOfferDTO);

        verify(mockValidator).validate(eq(fixtureOfferDTO));
    }

    @Test
    public void testProcessOfferInputDataValidationExceptionSuccess() {
        when(mockValidator.validate(eq(fixtureOfferDTO))).thenReturn(Collections.emptySet());

        unit.processOfferInputDataValidationException(fixtureOfferDTO);

        verify(mockValidator).validate(eq(fixtureOfferDTO));
    }

    @Test
    public void testProcessOfferInputDataValidationExceptionError() {
        expectedException.expect(instanceOf(VariableNotValidException.class));

        Set<ConstraintViolation<OfferDTO>> constraintViolations = Collections.singleton(mockOfferDTOConstraintViolation);
        when(mockValidator.validate(eq(fixtureOfferDTO))).thenReturn(constraintViolations);

        unit.processOfferInputDataValidationException(fixtureOfferDTO);

        verify(mockValidator).validate(eq(fixtureOfferDTO));
    }
}
