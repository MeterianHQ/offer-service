package com.ovoenergy.offer.manager.impl;

import com.google.common.collect.Sets;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.ValidationDTO;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.mapper.OfferMapper;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.key.MessageKeys;
import com.ovoenergy.offer.validation.key.ValidationCodeMessageKeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class OfferManagerImpl implements OfferManager {

    private final static String OFFER_CODE_FIELD_NAME = "offerCode";

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private MessageSource msgSource;

    @Override
    public OfferDTO getOfferById(Long id) {
        return OfferMapper.fromOfferDBEntityToDTO(offerRepository.findOneById(id));
    }

    @Override
    public OfferDTO createOffer(OfferDTO offerDTO) {
        ValidationDTO validationDTO = processOfferCodeValidation(offerDTO);
        if (validationDTO != null) {
            return validationDTO;
        }

        OfferDBEntity offerDBEntity = OfferMapper.fromOfferDTOTODBEntity(offerDTO);
        offerDBEntity.setId(null);
        offerDBEntity.setStatus(StatusType.ACTIVE);
        offerDBEntity.setUpdatedOn(LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());

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
    public Boolean validateOffer(String offerCode) {
        OfferDBEntity offerDBEntity = offerRepository.findOneByOfferCode(offerCode);
        if (null != offerDBEntity) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean applyToOffer(String offerCode, String emailAddress) {
        //TODO: TBD
        return null;
    }

    private ValidationDTO processOfferCodeValidation(OfferDTO offerDTO) {
        if(validateOffer(offerDTO.getOfferCode())) {
            Map<String, Set<ErrorMessageDTO>> validations = new HashMap<>();
            validations.put(OFFER_CODE_FIELD_NAME,
                    Sets.newHashSet(
                            new ErrorMessageDTO(
                                CodeKeys.NOT_UNIQUE_OFFER_CODE,
                                msgSource.getMessage(MessageKeys.NOT_UNIQUE_OFFER_CODE, null, LocaleContextHolder.getLocale()))));

            ValidationDTO validationDTO = new ValidationDTO(offerDTO);
            validationDTO.setConstraintViolations(validations);
            return validationDTO;
        }
        return null;

    }
}
