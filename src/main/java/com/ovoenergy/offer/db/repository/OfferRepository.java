package com.ovoenergy.offer.db.repository;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<OfferDBEntity, Long> {

    OfferDBEntity findOneById(Long id);

    OfferDBEntity findOneByOfferCodeIgnoreCase(String offerCode);
}
