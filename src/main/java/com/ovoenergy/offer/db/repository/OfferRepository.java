package com.ovoenergy.offer.db.repository;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface OfferRepository extends JpaRepository<OfferDBEntity, Long> {

    OfferDBEntity findOneByOfferCodeIgnoreCaseAndStatus(String offerCode, StatusType status);

    boolean existsByOfferCodeIgnoreCaseAndIdIsNot(String offerCode, Long id);

    OfferDBEntity findOneByOfferCodeIgnoreCase(String offerCode);

    List<OfferDBEntity> findAllByStatusAndExpiryDateLessThan(StatusType status, Long expiryDate);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE offer o SET o.status = 'EXPIRED', o.updatedOn = :now WHERE o.expiryDate < :nowMidnight AND o.status = 'ACTIVE'")
    int updateExpiredOffersStatus(@Param("now") Long now, @Param("nowMidnight") Long nowMidnight);

}
