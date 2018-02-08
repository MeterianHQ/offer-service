package com.ovoenergy.offer.dto;

import com.ovoenergy.offer.test.utils.BaseBeansTest;
import org.junit.Test;

public class DTOEntitiesTest extends BaseBeansTest {
    @Test
    public void testVerifyGettersAndSetters() {
        verify(OfferDTO.class);
        verify(OfferValidationDTO.class);
    }

    @Test
    public void testEquality() {
        equality(OfferDTO.class);
        equality(OfferValidationDTO.class);
    }

    @Test
    public void testHashCode() {
        hashCodeEquality(OfferDTO.class);
        hashCodeEquality(OfferValidationDTO.class);
    }
}
