package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferDTO;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class StartDateNotUpdatableValidatorTest extends AbstractConstraintValidatorTest {

    @InjectMocks
    private StartDateNotUpdatableValidator startDateNotUpdatableValidator;
    @Mock
    private OfferRepository offerRepository;

    @Test
    public void testIdIsNull() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setId(null);

        boolean valid = startDateNotUpdatableValidator.isValid(offerDTO, context);

        assertThat(valid, is(true));

        verifyZeroInteractions(offerRepository);
    }

    @Test
    public void testIdIsNotExist() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setId(1L);

        when(offerRepository.findOne(anyLong())).thenReturn(null);

        boolean valid = startDateNotUpdatableValidator.isValid(offerDTO, context);

        assertThat(valid, is(true));

        verify(offerRepository, only()).findOne(anyLong());
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testStartDateEquals() {
        Long startDate = 100L;

        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setId(1L);
        offerDTO.setStartDate(startDate);

        when(offerRepository.findOne(anyLong())).thenReturn(OfferDBEntity.builder().startDate(startDate).build());

        boolean valid = startDateNotUpdatableValidator.isValid(offerDTO, context);

        assertThat(valid, is(true));

        verify(offerRepository, only()).findOne(anyLong());
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testStartDateDifferent() {
        Long startDate = 100L;

        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setId(1L);
        offerDTO.setStartDate(startDate);

        when(offerRepository.findOne(anyLong())).thenReturn(OfferDBEntity.builder().startDate(99L).build());

        boolean valid = startDateNotUpdatableValidator.isValid(offerDTO, context);

        assertThat(valid, is(false));

        verify(offerRepository, only()).findOne(anyLong());
        verifyNoMoreInteractions(offerRepository);
    }
}