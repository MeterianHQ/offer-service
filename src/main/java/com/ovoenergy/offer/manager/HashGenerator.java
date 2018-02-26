package com.ovoenergy.offer.manager;

import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;

public interface HashGenerator {

    String generateHash(OfferRedeemDBEntity offerRedeemDBEntity);
}
