package com.ovoenergy.offer.db.repository;

import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRedeemRepository extends JpaRepository<OfferRedeemDBEntity, Long> {

    OfferRedeemDBEntity findByEmailAndOfferDBEntityId(String email, Long id);

    OfferRedeemDBEntity findByEmailAndOfferDBEntityIdAndHash(String email, Long id, String hash);

    OfferRedeemDBEntity findByEmailAndOfferDBEntityOfferCodeIgnoreCase(String email, String offerCode);
}
