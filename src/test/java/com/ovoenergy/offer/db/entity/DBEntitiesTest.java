package com.ovoenergy.offer.db.entity;

import com.flextrade.jfixture.JFixture;
import com.ovoenergy.offer.test.utils.BaseBeansTest;
import com.ovoenergy.offer.test.utils.UnitTest;
import org.junit.Test;


import static org.junit.Assert.assertTrue;

@UnitTest
public class DBEntitiesTest extends BaseBeansTest {

    @Test
    public void testVerifyGettersAndSetters() {
        verify(OfferDBEntity.class);
        verify(OfferRedeemDBEntity.class);
        verify(OfferRedeemId.class);
    }

    @Test
    public void testEquality() {
        equality(OfferDBEntity.class);
        equality(OfferRedeemDBEntity.class);
    }

    @Test
    public void testHashCode() {
        hashCodeEquality(OfferDBEntity.class);
        hashCodeEquality(OfferRedeemDBEntity.class);
        hashCodeEquality(OfferRedeemId.class);
   }

    /*
     * BaseBeans tester doesn't handle all cases. Need to test some Entities
     * manually.
     */
    @Test
    public void testCustomEquality() {
        JFixture fixture = new JFixture();
        OfferRedeemId offerRedeemId = fixture.create(OfferRedeemId.class);
        assertTrue(offerRedeemId.hashCode() != fixture.create(OfferRedeemId.class).hashCode());
        assertTrue(offerRedeemId.hashCode() == offerRedeemId.hashCode());
    }
}