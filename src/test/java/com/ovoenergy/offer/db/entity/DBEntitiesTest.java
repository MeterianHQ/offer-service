package com.ovoenergy.offer.db.entity;

import com.ovoenergy.offer.test.utils.BaseBeansTest;
import org.junit.Test;

public class DBEntitiesTest extends BaseBeansTest {

    @Test
    public void testVerifyGettersAndSetters() {
        verify(OfferDBEntity.class);
        verify(OfferRedeemDBEntity.class);
        verify(OfferRedeemEventDBEntity.class);
        verify(AuditDBEntity.class);
    }

    @Test
    public void testEquality() {
        equality(OfferDBEntity.class);
//        equality(OfferRedeemDBEntity.class);
//        equality(OfferRedeemEventDBEntity.class);
        equality(AuditDBEntity.class);
    }

    @Test
    public void testHashCode() {
        hashCodeEquality(OfferDBEntity.class);
//        hashCodeEquality(OfferRedeemDBEntity.class);
//        hashCodeEquality(OfferRedeemEventDBEntity.class);
        hashCodeEquality(AuditDBEntity.class);
    }
}
