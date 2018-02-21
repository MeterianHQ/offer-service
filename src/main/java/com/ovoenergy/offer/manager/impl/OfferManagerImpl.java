package com.ovoenergy.offer.manager.impl;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.manager.operation.OfferOperationsRegistry;
import com.ovoenergy.offer.mapper.OfferMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_INVALID;

@Service
public class OfferManagerImpl implements OfferManager {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferRedeemRepository offerRedeemRepository;

    @Autowired
    private OfferOperationsRegistry offerOperationsRegistry;

    @Override
    public OfferDTO getOfferById(Long id) {
        return OfferMapper.fromOfferDBEntityToDTO(offerRepository.findOneById(id));
    }

    @Override
    public OfferDTO createOffer(OfferDTO offerDTO) {
        OfferDBEntity offerDBEntity = offerOperationsRegistry.createOfferDBEntity(offerDTO);

        return OfferMapper.fromOfferDBEntityToDTO(offerRepository.save(offerDBEntity));
    }

    @Override
    public OfferDTO updateOffer(OfferDTO offerDTO, Long id) {
        OfferDBEntity oldOfferDBEntity = offerRepository.findOneById(id);
        OfferDBEntity offerDBEntity = offerOperationsRegistry.updateOfferDBEntity(oldOfferDBEntity, offerDTO);

        return OfferMapper.fromOfferDBEntityToDTO(offerRepository.save(offerDBEntity));
    }

    @Override
    public OfferDTO deleteOffer(Long id) {
        //TODO: TBD
        return null;
    }

    @Override
    public List<OfferDTO> getAllOffers() {
        return offerRepository.findAll(new Sort(Sort.Direction.DESC, "updatedOn"))
                .stream()
                .map(OfferMapper::fromOfferDBEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean verifyOffer(String offerCode) {
        OfferDBEntity offerDBEntity = offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(offerCode, StatusType.ACTIVE);
        if (offerDBEntity == null) {
            throw new VariableNotValidException(OFFER_INVALID);
        } else {
            offerOperationsRegistry.processOfferDBEntityValidation(offerDBEntity);
        }
        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public OfferApplyDTO applyUserToOffer(String offerCode, String emailAddress) {
        OfferDBEntity offerDBEntity = offerOperationsRegistry.processOfferDBEntityValidation(offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(offerCode, StatusType.ACTIVE));
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
}
