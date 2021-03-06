package com.ovoenergy.offer.manager.operation;

import com.google.common.primitives.Longs;
import com.ovoenergy.offer.db.entity.ChannelType;
import com.ovoenergy.offer.db.entity.EligibilityCriteriaType;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferType;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.entity.SupplierType;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.mapper.OfferMapper;
import org.springframework.stereotype.Component;

@Component
public class ActiveOfferStrategy extends OfferBaseStrategy {

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
    public OfferDBEntity updateOfferDBEntity(OfferDBEntity oldOfferDBEntity, OfferDTO offerDTO) {
        String maxOfferRedemptions = offerDTO.getMaxOfferRedemptions();
        String value = offerDTO.getValue();
        return OfferDBEntity
                .builder()
                .id(oldOfferDBEntity.getId())
                .offerCode(offerDTO.getOfferCode())
                .offerName(offerDTO.getOfferName())
                .offerType(OfferType.byValue(offerDTO.getOfferType()))
                .description(offerDTO.getDescription())
                .channel(ChannelType.byValue(offerDTO.getChannel()))
                .eligibilityCriteria(EligibilityCriteriaType.byValue(offerDTO.getEligibilityCriteria()))
                .startDate(StatusType.DRAFT.equals(oldOfferDBEntity.getStatus()) ? offerDTO.getStartDate() : oldOfferDBEntity.getStartDate())
                .expiryDate(offerDTO.getExpiryDate())
                .isExpirable(offerDTO.getIsExpirable())
                .maxOfferRedemptions(maxOfferRedemptions == null ? null : Longs.tryParse(maxOfferRedemptions))
                .supplier(SupplierType.byValue(offerDTO.getSupplier()))
                .value(value == null ? null : Longs.tryParse(value))
                .actualOfferRedemptions(oldOfferDBEntity.getActualOfferRedemptions())
                .status(StatusType.ACTIVE)
                .updatedOn(jdbcHelper.lookupCurrentDbTime().getTime())
                .build();
    }
}
