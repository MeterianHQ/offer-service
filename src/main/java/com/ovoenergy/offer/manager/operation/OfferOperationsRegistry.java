package com.ovoenergy.offer.manager.operation;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_STATUS_TYPE_NOT_VALID;

@Component
public class OfferOperationsRegistry {

    @Autowired
    private ActiveOfferStrategy activeOfferStrategy;

    @Autowired
    private DraftOfferStrategy draftOfferStrategy;

    private Map<StatusType, OfferBaseStrategy> OfferStrategiesRegistry = new HashMap<>();

    @PostConstruct
    public void init() {
        OfferStrategiesRegistry.put(StatusType.ACTIVE, activeOfferStrategy);
        OfferStrategiesRegistry.put(StatusType.DRAFT, draftOfferStrategy);
    }

    public OfferDBEntity createOfferDBEtity(OfferDTO offerDTO) {
        StatusType statusType = processStatusType(offerDTO.getStatus());
        OfferBaseStrategy offerBaseStrategy = OfferStrategiesRegistry.get(statusType);
        return offerBaseStrategy.createOfferDBEntity(offerDTO);
    }

    public OfferDBEntity updateOfferDBEntity(OfferDBEntity offerDBEntity, OfferDTO offerDTO) {
        StatusType statusType = processStatusType(offerDTO.getStatus());
        OfferBaseStrategy offerBaseStrategy = OfferStrategiesRegistry.get(statusType);
        return offerBaseStrategy.updateOfferDBEntity(offerDBEntity, offerDTO);
    }

    public OfferDBEntity processOfferDBEntityValidation(OfferDBEntity offerDBEntity) {
        StatusType statusType = processStatusType(offerDBEntity.getStatus().name());
        OfferBaseStrategy offerBaseStrategy = OfferStrategiesRegistry.get(statusType);
        return offerBaseStrategy.processOfferDBEntityValidation(offerDBEntity);
    }

    public OfferRedeemDBEntity createOfferRedeemDBEntity(OfferDBEntity offerDBEntity, String emailAddress) {
        StatusType statusType = processStatusType(offerDBEntity.getStatus().name());
        OfferBaseStrategy offerBaseStrategy = OfferStrategiesRegistry.get(statusType);
        return offerBaseStrategy.createOfferRedeemDBEntity(offerDBEntity.getId(), emailAddress);
    }

    private StatusType processStatusType(String statusTypeStr) {
        StatusType statusType;
        try {
            statusType = StatusType.valueOf(statusTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VariableNotValidException(OFFER_STATUS_TYPE_NOT_VALID);
        }
        return statusType;
    }
}