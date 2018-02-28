package com.ovoenergy.offer.integration.data;

import com.ovoenergy.offer.db.entity.ChannelType;
import com.ovoenergy.offer.db.entity.EligibilityCriteriaType;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferType;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.entity.SupplierType;
import com.ovoenergy.offer.dto.OfferDTO;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;

@UtilityClass
public class TestData {

    private static final String TEST_VALID_DESCRIPTION = "Valid description 100%";
    private static final String TEST_VALID_NAME = "Valid name 100%";
    private static final String TEST_VALID_CODE = "validCODE";
    private static final String TEST_VALID_SUPPLIER = "Amazon";
    private static final String TEST_VALID_OFFER_TYPE = "Giftcard";
    private static final String TEST_VALID_ELIGIBILITY_CRITERIA = "SSD";
    private static final String TEST_VALID_CHANEL = "Email";
    private static final Long TEST_VALID_MAX_VALUE = 333L;
    private static final Long TEST_VALID_MAX_REDEMPTION = 88888888L;
    private static final Long TEST_VALID_DATE_IN_FUTURE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    private static final Long TEST_VALID_EXPIRY_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    private static final Long TEST_VALID_UPDATE_ON_DATE = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    public static OfferDBEntity prepareForTestValidOfferDBEntity(StatusType statusType) {
        OfferDBEntity offerDBEntity = new OfferDBEntity();
        offerDBEntity.setDescription(TEST_VALID_DESCRIPTION);
        offerDBEntity.setOfferName(TEST_VALID_NAME);
        offerDBEntity.setOfferCode(TEST_VALID_CODE);
        offerDBEntity.setSupplier(SupplierType.byValue(TEST_VALID_SUPPLIER));
        offerDBEntity.setOfferType(OfferType.byValue(TEST_VALID_OFFER_TYPE));
        offerDBEntity.setValue(TEST_VALID_MAX_VALUE);
        offerDBEntity.setMaxOfferRedemptions(TEST_VALID_MAX_REDEMPTION);
        offerDBEntity.setEligibilityCriteria(EligibilityCriteriaType.byValue(TEST_VALID_ELIGIBILITY_CRITERIA));
        offerDBEntity.setChannel(ChannelType.byValue(TEST_VALID_CHANEL));
        offerDBEntity.setStartDate(TEST_VALID_DATE_IN_FUTURE);
        offerDBEntity.setExpiryDate(TEST_VALID_EXPIRY_DATE);
        offerDBEntity.setIsExpirable(true);
        offerDBEntity.setStatus(statusType);
        offerDBEntity.setUpdatedOn(TEST_VALID_UPDATE_ON_DATE);
        offerDBEntity.setId(1L);
        return offerDBEntity;
    }

    public static OfferDTO prepareForValidOfferDTO(StatusType statusType) {
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setDescription(TEST_VALID_DESCRIPTION);
        offerToValidate.setOfferName(TEST_VALID_NAME);
        offerToValidate.setOfferCode(TEST_VALID_CODE);
        offerToValidate.setSupplier(TEST_VALID_SUPPLIER);
        offerToValidate.setOfferType(TEST_VALID_OFFER_TYPE);
        offerToValidate.setValue(TEST_VALID_MAX_VALUE);
        offerToValidate.setMaxOfferRedemptions(TEST_VALID_MAX_REDEMPTION);
        offerToValidate.setEligibilityCriteria(TEST_VALID_ELIGIBILITY_CRITERIA);
        offerToValidate.setChannel(TEST_VALID_CHANEL);
        offerToValidate.setStartDate(TEST_VALID_DATE_IN_FUTURE);
        offerToValidate.setExpiryDate(TEST_VALID_EXPIRY_DATE);
        offerToValidate.setIsExpirable(true);
        offerToValidate.setStatus(statusType.name());
        return offerToValidate;
    }
}
