package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferDTO;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class OfferCodeValidatorTest extends AbstractConstraintValidatorTest {

    @InjectMocks
    private OfferCodeValidator offerCodeValidator;
    @Mock
    private OfferRepository offerRepository;

    @Test
    public void testIdIsNullCodeNotExists() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setId(null);
        offerDTO.setOfferCode("Offer code");
        when(offerRepository.findOneByOfferCodeIgnoreCase(anyString())).thenReturn(null);

        boolean valid = offerCodeValidator.isValid(offerDTO, context);

        assertThat(valid, is(true));

        verify(offerRepository, only()).findOneByOfferCodeIgnoreCase(anyString());
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testIdIsNullCodeExists() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setId(null);
        offerDTO.setOfferCode("Offer code");
        when(offerRepository.findOneByOfferCodeIgnoreCase(anyString())).thenReturn(new OfferDBEntity());

        boolean valid = offerCodeValidator.isValid(offerDTO, context);

        assertThat(valid, is(false));

        verify(offerRepository, only()).findOneByOfferCodeIgnoreCase(anyString());
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testIdIsNotNullCodeNotExists() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setId(1L);
        offerDTO.setOfferCode("Offer code");
        when(offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong())).thenReturn(false);

        boolean valid = offerCodeValidator.isValid(offerDTO, context);

        assertThat(valid, is(true));

        verify(offerRepository, only()).existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong());
        verifyNoMoreInteractions(offerRepository);
    }

    @Test
    public void testIdIsNotNullCodeExists() {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setId(1L);
        offerDTO.setOfferCode("Offer code");
        when(offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong())).thenReturn(true);

        boolean valid = offerCodeValidator.isValid(offerDTO, context);

        assertThat(valid, is(false));

        verify(offerRepository, only()).existsByOfferCodeIgnoreCaseAndIdIsNot(anyString(), anyLong());
        verifyNoMoreInteractions(offerRepository);
    }
}