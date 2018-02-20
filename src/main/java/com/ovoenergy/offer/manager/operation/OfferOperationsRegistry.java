package com.ovoenergy.offer.manager.operation;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_STATUS_TYPE_NOT_VALID;

@Component
public class OfferOperationsRegistry {

    private final Map<StatusType, OfferBaseStrategy> offerStrategiesRegistry = new EnumMap<>(StatusType.class);

    @Autowired
    private ActiveOfferStrategy activeOfferStrategy;

    @Autowired
    private DraftOfferStrategy draftOfferStrategy;

    @PostConstruct
    public void init() {
        offerStrategiesRegistry.put(StatusType.ACTIVE, activeOfferStrategy);
        offerStrategiesRegistry.put(StatusType.DRAFT, draftOfferStrategy);
    }

    public OfferDBEntity createOfferDBEntity(OfferDTO offerDTO) {
        StatusType statusType = processStatusType(offerDTO.getStatus());
        OfferBaseStrategy offerBaseStrategy = offerStrategiesRegistry.get(statusType);
        return offerBaseStrategy.createOfferDBEntity(offerDTO);
    }

    public OfferDBEntity updateOfferDBEntity(OfferDBEntity offerDBEntity, OfferDTO offerDTO) {
        StatusType statusType = processStatusType(offerDTO.getStatus());
        OfferBaseStrategy offerBaseStrategy = offerStrategiesRegistry.get(statusType);
        offerDBEntity.setActualOfferRedemptions(offerDBEntity.getActualOfferRedemptions());
        return offerBaseStrategy.updateOfferDBEntity(offerDBEntity, offerDTO);
    }

    public OfferDBEntity processOfferDBEntityValidation(OfferDBEntity offerDBEntity) {
        StatusType statusType = processStatusType(offerDBEntity.getStatus().name());
        OfferBaseStrategy offerBaseStrategy = offerStrategiesRegistry.get(statusType);
        return offerBaseStrategy.processOfferDBEntityValidation(offerDBEntity);
    }

    public OfferRedeemDBEntity createOfferRedeemDBEntity(OfferDBEntity offerDBEntity, String emailAddress) {
        StatusType statusType = processStatusType(offerDBEntity.getStatus().name());
        OfferBaseStrategy offerBaseStrategy = offerStrategiesRegistry.get(statusType);
        return offerBaseStrategy.createOfferRedeemDBEntity(offerDBEntity.getId(), emailAddress);
    }

    private StatusType processStatusType(String statusTypeStr) {
        try {
            return StatusType.valueOf(statusTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VariableNotValidException(OFFER_STATUS_TYPE_NOT_VALID);
        }
    }
}