package com.ovoenergy.offer.manager.impl;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.manager.HashGenerator;
import org.springframework.stereotype.Service;

@Service
public class HashGeneratorImpl implements HashGenerator {

    @Override
    public String generateHash(OfferRedeemDBEntity offerRedeemDBEntity) {
        return Hashing.sha256()
                .newHasher()
                .putLong(offerRedeemDBEntity.getId())
                .putLong(offerRedeemDBEntity.getOfferDBEntity().getId())
                .putString(offerRedeemDBEntity.getEmail(), Charsets.UTF_8)
                .hash()
                .toString();
    }
}
