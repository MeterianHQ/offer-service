package com.ovoenergy.offer.manager.operation;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.mapper.OfferMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActiveOfferStrategy extends OfferBaseStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveOfferStrategy.class);

    @Override
    public OfferDBEntity createOfferDBEntity(OfferDTO offerDTO) {
        OfferDBEntity offerDBEntity = OfferMapper.fromOfferDTOTODBEntity(offerDTO);
        offerDBEntity.setId(null);
        offerDBEntity.setStatus(StatusType.ACTIVE);
        offerDBEntity.setActualOfferRedemptions(0L);
        offerDBEntity.setUpdatedOn(jdbcHelper.lookupCurrentDbTime().getTime());
        return offerDBEntity;
    }

    @Override
    public OfferDBEntity updateOfferDBEntity(OfferDBEntity ruleDBDoc, OfferDTO offerDTO) {
        return null;
    }

    @Override
    public boolean anyChangesInOfferDetected(OfferDBEntity ruleDBDoc, OfferDTO offerDTO) {
        return false;
    }
}
