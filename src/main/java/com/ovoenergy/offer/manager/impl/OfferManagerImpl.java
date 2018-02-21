package com.ovoenergy.offer.manager.impl;

import com.google.common.collect.Sets;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.manager.operation.OfferOperationsRegistry;
import com.ovoenergy.offer.mapper.OfferMapper;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.key.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_INVALID;

@Service
public class OfferManagerImpl implements OfferManager {

    private final static String OFFER_CODE_FIELD_NAME = "offerCode";

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferRedeemRepository offerRedeemRepository;

    @Autowired
    private OfferOperationsRegistry offerOperationsRegistry;

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

        OfferDBEntity offerDBEntity = offerOperationsRegistry.createOfferDBEtity(offerDTO);

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

        offerRepository.findAll(new Sort(Sort.Direction.DESC, "updatedOn")).forEach(off ->  offers.add(OfferMapper.fromOfferDBEntityToDTO(off)));

        return offers;
    }

    @Override
    public Boolean verifyOffer(String offerCode) {
        fetchActiveOfferByOfferCode(offerCode);
        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public OfferApplyDTO applyUserToOffer(String offerCode, String emailAddress) {
        OfferDBEntity offerDBEntity =  fetchActiveOfferByOfferCode(offerCode);
        offerDBEntity.setActualOfferRedemptions(offerDBEntity.getActualOfferRedemptions() + 1);

        OfferRedeemDBEntity offerRedeemDBEntity = offerRedeemRepository.save(offerOperationsRegistry.createOfferRedeemDBEntity(offerDBEntity, emailAddress));
        offerRepository.save(offerDBEntity);

        return OfferApplyDTO
                .builder()
                .email(emailAddress)
                .offerCode(offerCode)
                .updatedOn(offerRedeemDBEntity.getUpdatedOn())
                .build();
    }

    private OfferDBEntity fetchActiveOfferByOfferCode(String offerCode) {
        OfferDBEntity offerDBEntity = offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(offerCode, StatusType.ACTIVE);
        if(offerDBEntity == null) {
            throw new VariableNotValidException(OFFER_INVALID);
        } else {
            offerOperationsRegistry.processOfferDBEntityValidation(offerDBEntity);
        }
        return offerDBEntity;
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
}
