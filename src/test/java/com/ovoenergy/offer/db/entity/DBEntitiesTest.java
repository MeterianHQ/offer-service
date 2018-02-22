package com.ovoenergy.offer.db.entity;

import com.flextrade.jfixture.JFixture;
import com.ovoenergy.offer.test.utils.BaseBeansTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DBEntitiesTest extends BaseBeansTest {

    @Test
    public void testVerifyGettersAndSetters() {
        verify(OfferDBEntity.class);
        verify(OfferRedeemDBEntity.class);
        verify(OfferRedeemId.class);
        verify(AuditDBEntity.class);
    }

    @Test
    public void testEquality() {
        equality(OfferDBEntity.class);
        equality(OfferRedeemDBEntity.class);
        equality(AuditDBEntity.class);
    }

    @Test
    public void testHashCode() {
        hashCodeEquality(OfferDBEntity.class);
        hashCodeEquality(OfferRedeemDBEntity.class);
        hashCodeEquality(OfferRedeemId.class);
        hashCodeEquality(AuditDBEntity.class);
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
        assertEquals(offerRedeemId.hashCode(), offerRedeemId.hashCode());
    }
}
