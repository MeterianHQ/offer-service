package com.ovoenergy.offer.manager.impl;

import com.google.common.collect.Sets;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.mapper.OfferMapper;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.key.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_EXPIRED;
import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_INVALID;

@Service
public class OfferManagerImpl implements OfferManager {

    private final static String OFFER_CODE_FIELD_NAME = "offerCode";

    @Autowired
    private JdbcHelper jdbcHelper;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferRedeemRepository offerRedeemRepository;

    @Autowired
    private MessageSource msgSource;

    @Override
    public OfferDTO getOfferById(Long id) {
        return OfferMapper.fromOfferDBEntityToDTO(offerRepository.findOneById(id));
    }

    @Override
    public OfferDTO createOffer(OfferDTO offerDTO) {
        OfferValidationDTO validationDTO = processOfferCodeInputValidation(offerDTO);
        if (validationDTO != null) {
            return validationDTO;
        }

        OfferDBEntity offerDBEntity = OfferMapper.fromOfferDTOTODBEntity(offerDTO);
        offerDBEntity.setId(null);
        offerDBEntity.setStatus(StatusType.ACTIVE);
        offerDBEntity.setActualOfferRedemptions(0L);
        offerDBEntity.setUpdatedOn(jdbcHelper.lookupCurrentDbTime().getTime());

        return OfferMapper
                .fromOfferDBEntityToDTO(
                        offerRepository.save(offerDBEntity));
    }

    @Override
    public OfferDTO updateOffer(OfferDTO offerDTO, Long id) {
        //TODO: TBD
        return null;
    }

    @Override
    public OfferDTO deleteOffer(Long id) {
        //TODO: TBD
        return null;
    }

    @Override
    public List<OfferDTO> getAllOffers() {
        List<OfferDTO> offers = new ArrayList<>();

        offerRepository.findAll().forEach(off ->  offers.add(OfferMapper.fromOfferDBEntityToDTO(off)));

        return offers;
    }

    @Override
    public Boolean verifyOffer(String offerCode) {
        processOfferDBEntityValidation(offerCode);
        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public OfferApplyDTO applyUserToOffer(String offerCode, String emailAddress) {
        Long currentDbTimeMilliseconds = jdbcHelper.lookupCurrentDbTime().getTime();
        OfferDBEntity offerDBEntity = processOfferDBEntityValidation(offerCode);
        offerDBEntity.setActualOfferRedemptions(offerDBEntity.getActualOfferRedemptions() + 1);
        offerDBEntity.setUpdatedOn(currentDbTimeMilliseconds);

        OfferRedeemDBEntity offerRedeemDBEntity = offerRedeemRepository.save(new OfferRedeemDBEntity(offerDBEntity.getId(), emailAddress, currentDbTimeMilliseconds));
        offerRepository.save(offerDBEntity);

        return OfferApplyDTO
                .builder()
                .email(emailAddress)
                .offerCode(offerCode)
                .updatedOn(offerRedeemDBEntity.getUpdatedOn())
                .build();
    }

    private OfferValidationDTO processOfferCodeInputValidation(OfferDTO offerDTO) {
        OfferDBEntity offerDBEntity = offerRepository.findOneByOfferCodeIgnoreCase(offerDTO.getOfferCode());
        if(offerDBEntity != null) {
            Map<String, Set<ErrorMessageDTO>> validations = new HashMap<>();
            validations.put(OFFER_CODE_FIELD_NAME,
                    Sets.newHashSet(
                            new ErrorMessageDTO(
                                CodeKeys.NOT_UNIQUE_OFFER_CODE,
                                msgSource.getMessage(MessageKeys.NOT_UNIQUE_OFFER_CODE, null, LocaleContextHolder.getLocale()))));

            OfferValidationDTO validationDTO = new OfferValidationDTO(offerDTO);
            validationDTO.setConstraintViolations(validations);
            return validationDTO;
        }
        return null;

    }

    private OfferDBEntity processOfferDBEntityValidation(String offerCode) {
        OfferDBEntity offerDBEntity = offerRepository.findOneByOfferCodeIgnoreCase(offerCode);
        if (null == offerDBEntity || !isStartDateValid(offerDBEntity) || !maxRedemptionsNotExceeded(offerDBEntity)) {
            throw new VariableNotValidException(OFFER_INVALID);
        } else if (!isExpiryDateValid(offerDBEntity)) {
            throw new VariableNotValidException(OFFER_EXPIRED);
        }
        return offerDBEntity;
    }

    private Boolean maxRedemptionsNotExceeded(OfferDBEntity offerDBEntity) {
        return (offerDBEntity.getActualOfferRedemptions() < offerDBEntity.getMaxOfferRedemptions());
    }

    private Boolean isStartDateValid(OfferDBEntity offerDBEntity) {
        Long now = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        return (offerDBEntity.getStartDate() <= now);
    }

    private Boolean isExpiryDateValid(OfferDBEntity offerDBEntity) {
        Long now = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        return (!offerDBEntity.getIsExpirable() || offerDBEntity.getExpiryDate() >= now);
    }

}
