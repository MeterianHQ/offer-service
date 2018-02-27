package com.ovoenergy.offer.manager.impl;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemDBEntity;
import com.ovoenergy.offer.db.entity.OfferRedeemStatusType;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.db.repository.OfferRedeemRepository;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferLinkGenerateDTO;
import com.ovoenergy.offer.dto.OfferRedeemInfoDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.manager.HashGenerator;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.manager.operation.OfferOperationsRegistry;
import com.ovoenergy.offer.mapper.OfferMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_EXPIRED;
import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_INVALID;

@Service
public class OfferManagerImpl implements OfferManager {

    // TODO: 2/23/18 refactoring
    private static final String LINK_TEMPLATE = "http://localhost:8080/offers/redemption/link/%1$s?user=%2$s&offer_id=%3$d";

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferRedeemRepository offerRedeemRepository;

    @Autowired
    private OfferOperationsRegistry offerOperationsRegistry;

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private JdbcHelper jdbcHelper;

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
        fetchActiveOfferByOfferCode(offerCode);
        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public OfferApplyDTO applyUserToOffer(String offerCode, String emailAddress) {
        OfferRedeemDBEntity existingOfferRedeemDBEntity = offerRedeemRepository.findByEmailAndOfferDBEntityOfferCodeIgnoreCase(emailAddress, offerCode);
        if (existingOfferRedeemDBEntity != null) {
            return OfferApplyDTO
                    .builder()
                    .email(emailAddress)
                    .offerCode(offerCode)
                    .updatedOn(existingOfferRedeemDBEntity.getUpdatedOn())
                    .build();
        }
        OfferDBEntity offerDBEntity = fetchActiveOfferByOfferCode(offerCode);
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

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public String generateOfferLink(OfferLinkGenerateDTO offerLinkGenerateDTO) {
        OfferRedeemDBEntity offerRedeemDBEntity = offerRedeemRepository.findByEmailAndOfferDBEntityId(offerLinkGenerateDTO.getEmail(), offerLinkGenerateDTO.getOfferId());
        if (offerRedeemDBEntity.getStatus() == OfferRedeemStatusType.CREATED) {
            String hash = hashGenerator.generateHash(offerRedeemDBEntity);
            offerRedeemDBEntity.setHash(hash);
            offerRedeemDBEntity.setUpdatedOn(jdbcHelper.lookupCurrentDbTime().getTime());
            offerRedeemDBEntity.setStatus(OfferRedeemStatusType.GENERATED);
            offerRedeemRepository.saveAndFlush(offerRedeemDBEntity);
        }
        return String.format(LINK_TEMPLATE, offerRedeemDBEntity.getHash(), offerLinkGenerateDTO.getEmail(), offerLinkGenerateDTO.getOfferId());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public OfferRedeemInfoDTO getOfferRedeemInfo(String hash, String email, Long id) {
        OfferRedeemDBEntity offerRedeemDBEntity = offerRedeemRepository.findByEmailAndOfferDBEntityIdAndHash(email, id, hash);
        offerRedeemDBEntity.setStatus(OfferRedeemStatusType.CLICKED);
        offerRedeemDBEntity.setUpdatedOn(jdbcHelper.lookupCurrentDbTime().getTime());
        offerRedeemRepository.saveAndFlush(offerRedeemDBEntity);
        return OfferRedeemInfoDTO.builder()
                .email(email)
                .updatedOn(offerRedeemDBEntity.getUpdatedOn())
                .offerCode(offerRedeemDBEntity.getOfferDBEntity().getOfferCode())
                .offerName(offerRedeemDBEntity.getOfferDBEntity().getOfferName())
                .build();
    }

    private OfferDBEntity fetchActiveOfferByOfferCode(String offerCode) {
        OfferDBEntity offerDBEntity = offerRepository.findOneByOfferCodeIgnoreCase(offerCode);
        if (offerDBEntity == null || StatusType.DRAFT.equals(offerDBEntity.getStatus())) {
            throw new VariableNotValidException(OFFER_INVALID);
        } else if (StatusType.EXPIRED.equals(offerDBEntity.getStatus())) {
            throw new VariableNotValidException(OFFER_EXPIRED);
        } else {
            offerOperationsRegistry.processOfferDBEntityValidation(offerDBEntity);
        }
        return offerDBEntity;
    }
}
