package com.ovoenergy.offer.manager.operation;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.mapper.OfferMapper;
import org.springframework.stereotype.Component;

@Component
public class DraftOfferStrategy extends OfferBaseStrategy {

    @Override
    public OfferDBEntity createOfferDBEntity(OfferDTO offerDTO) {
        OfferDBEntity offerDBEntity = OfferMapper.fromOfferDTOTODBEntity(offerDTO);
        offerDBEntity.setId(null);
        offerDBEntity.setStatus(StatusType.DRAFT);
        offerDBEntity.setActualOfferRedemptions(0L);
        offerDBEntity.setUpdatedOn(jdbcHelper.lookupCurrentDbTime().getTime());
        return offerDBEntity;
    }

    @Override
    public OfferDBEntity updateOfferDBEntity(OfferDBEntity ruleDBDoc, OfferDTO offerDTO) {
        OfferDBEntity offerDBEntity = OfferMapper.fromOfferDTOTODBEntity(offerDTO);
        offerDBEntity.setStatus(StatusType.DRAFT);
        offerDBEntity.setActualOfferRedemptions(ruleDBDoc.getActualOfferRedemptions());
        offerDBEntity.setUpdatedOn(jdbcHelper.lookupCurrentDbTime().getTime());
        return offerDBEntity;
    }

    @Override
    public boolean anyChangesInOfferDetected(OfferDBEntity ruleDBDoc, OfferDTO offerDTO) {
        return false;
    }
}
