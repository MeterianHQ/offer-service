package com.ovoenergy.offer.manager.operation;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemStatusType;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_EXPIRED;
import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_INVALID;

public abstract class OfferBaseStrategy {

    @Autowired
    protected JdbcHelper jdbcHelper;

    public abstract OfferDBEntity createOfferDBEntity(OfferDTO offerDTO);

    public abstract OfferDBEntity updateOfferDBEntity(OfferDBEntity oldOfferDBEntity, OfferDTO offerDTO);

    public abstract boolean anyChangesInOfferDetected(OfferDBEntity ruleDBDoc, OfferDTO offerDTO);

    public OfferDBEntity processOfferDBEntityValidation(OfferDBEntity offerDBEntity) {
        Long currentDbTimeMidnightMilliseconds = getCurrentDbTimeMidnightMilliseconds();

        if (null == offerDBEntity || !isStartDateValid(offerDBEntity, currentDbTimeMidnightMilliseconds) || !maxRedemptionsNotExceeded(offerDBEntity)) {
            throw new VariableNotValidException(OFFER_INVALID);
        } else if (!isExpiryDateValid(offerDBEntity, currentDbTimeMidnightMilliseconds)) {
            throw new VariableNotValidException(OFFER_EXPIRED);
        }
        return offerDBEntity;
    }

    public OfferRedeemDBEntity createOfferRedeemDBEntity(OfferDBEntity offerDBEntity, String emailAddress) {
        Long currentDbTimeMidnightMilliseconds = getCurrentDbTimeMidnightMilliseconds();
        return OfferRedeemDBEntity.builder()
                .offerDBEntity(offerDBEntity)
                .email(emailAddress)
                .updatedOn(currentDbTimeMidnightMilliseconds)
                .status(OfferRedeemStatusType.CREATED)
                .build();
    }

    private Long getCurrentDbTimeMidnightMilliseconds() {
        return LocalDateTime.of(jdbcHelper.lookupCurrentDbTime().toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), LocalTime.MIDNIGHT).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
    }

    private Boolean maxRedemptionsNotExceeded(OfferDBEntity offerDBEntity) {
        return (offerDBEntity.getActualOfferRedemptions() < offerDBEntity.getMaxOfferRedemptions());
    }

    private Boolean isStartDateValid(OfferDBEntity offerDBEntity, Long currentMidnightTime) {
        return (offerDBEntity.getStartDate() <= currentMidnightTime);
    }

    private Boolean isExpiryDateValid(OfferDBEntity offerDBEntity, Long currentMidnightTime) {
        return (!offerDBEntity.getIsExpirable() || offerDBEntity.getExpiryDate() >= currentMidnightTime);
    }
}
