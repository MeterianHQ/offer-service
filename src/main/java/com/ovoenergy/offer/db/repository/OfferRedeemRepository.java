package com.ovoenergy.offer.db.repository;

import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRedeemRepository extends  JpaRepository<OfferRedeemDBEntity, OfferRedeemId>{
}
