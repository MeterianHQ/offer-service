package com.ovoenergy.offer.db.repository;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface OfferRepository extends JpaRepository<OfferDBEntity, Long> {

    OfferDBEntity findOneById(Long id);

    OfferDBEntity findOneByOfferCodeIgnoreCaseAndStatus(String offerCode, StatusType status);
}
