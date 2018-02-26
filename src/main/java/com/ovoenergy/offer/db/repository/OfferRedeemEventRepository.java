package com.ovoenergy.offer.db.repository;

import com.ovoenergy.offer.db.entity.OfferRedeemEventDBEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRedeemEventRepository extends JpaRepository<OfferRedeemEventDBEntity, Long> {
}
