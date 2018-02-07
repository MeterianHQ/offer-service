package com.ovoenergy.offer.mapper;

import com.ovoenergy.offer.db.entity.*;
import com.ovoenergy.offer.dto.OfferDTO;
import org.apache.el.lang.ELArithmetic;

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
                .maxOfferRedemptions(offerDBEntity.getMaxOfferRedemptions())
                .supplier(offerDBEntity.getSupplier().value())
                .value(offerDBEntity.getValue())
                .id(offerDBEntity.getId())
                .actualOfferRedemptions(0L)
                .status(offerDBEntity.getStatus().name())
                .updatedOn(offerDBEntity.getUpdatedOn())
                .build();
    }

    public static OfferDBEntity fromOfferDTOTODBEntity(OfferDTO offerDTO) {
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
                .maxOfferRedemptions(offerDTO.getMaxOfferRedemptions())
                .supplier(SupplierType.byValue(offerDTO.getSupplier()))
                .value(offerDTO.getValue())
                .id(offerDTO.getId())
                .build();
    }
}
