package com.ovoenergy.offer.mapper;

import com.google.common.primitives.Longs;
import com.ovoenergy.offer.db.entity.ChannelType;
import com.ovoenergy.offer.db.entity.EligibilityCriteriaType;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferType;
import com.ovoenergy.offer.db.entity.SupplierType;
import com.ovoenergy.offer.dto.OfferDTO;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OfferMapper {

    public static OfferDTO fromOfferDBEntityToDTO(OfferDBEntity offerDBEntity) {
        return OfferDTO
                .builder()
                .offerCode(offerDBEntity.getOfferCode())
                .offerName(offerDBEntity.getOfferName())
                .offerType(offerDBEntity.getOfferType().value())
                .description(offerDBEntity.getDescription())
                .channel(offerDBEntity.getChannel().value())
                .eligibilityCriteria(offerDBEntity.getEligibilityCriteria().value())
                .startDate(offerDBEntity.getStartDate())
                .expiryDate(offerDBEntity.getExpiryDate())
                .isExpirable(offerDBEntity.getIsExpirable())
                .maxOfferRedemptions(offerDBEntity.getMaxOfferRedemptions() == null ? null : offerDBEntity.getMaxOfferRedemptions().toString())
                .supplier(offerDBEntity.getSupplier().value())
                .value(offerDBEntity.getValue() == null ? null : offerDBEntity.getValue().toString())
                .id(offerDBEntity.getId())
                .actualOfferRedemptions(offerDBEntity.getActualOfferRedemptions())
                .linksRedeemed(offerDBEntity.getLinksRedeemed())
                .status(offerDBEntity.getStatus().name())
                .updatedOn(offerDBEntity.getUpdatedOn())
                .build();
    }

    public static OfferDBEntity fromOfferDTOTODBEntity(OfferDTO offerDTO) {
        String maxOfferRedemptions = offerDTO.getMaxOfferRedemptions();
        String value = offerDTO.getValue();
        return OfferDBEntity
                .builder()
                .offerCode(offerDTO.getOfferCode())
                .offerName(offerDTO.getOfferName())
                .offerType(OfferType.byValue(offerDTO.getOfferType()))
                .description(offerDTO.getDescription())
                .channel(ChannelType.byValue(offerDTO.getChannel()))
                .eligibilityCriteria(EligibilityCriteriaType.byValue(offerDTO.getEligibilityCriteria()))
                .startDate(offerDTO.getStartDate())
                .expiryDate(offerDTO.getExpiryDate())
                .isExpirable(offerDTO.getIsExpirable())
                .maxOfferRedemptions(maxOfferRedemptions == null ? null : Longs.tryParse(maxOfferRedemptions))
                .linksRedeemed(offerDTO.getLinksRedeemed())
                .supplier(SupplierType.byValue(offerDTO.getSupplier()))
                .value(value == null ? null : Longs.tryParse(value))
                .id(offerDTO.getId())
                .build();
    }
}
