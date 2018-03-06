package com.ovoenergy.offer.manager.impl;

import com.ovoenergy.offer.amazon.AmazonStubbedVoucher;
import com.ovoenergy.offer.config.RedemptionLinkProperties;
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
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.manager.HashGenerator;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.manager.operation.OfferOperationsRegistry;
import com.ovoenergy.offer.manager.redirect.GetVoucherRedirectHandler;
import com.ovoenergy.offer.mapper.OfferMapper;
import com.ovoenergy.offer.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_EXPIRED;
import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_INVALID;

@Service
public class OfferManagerImpl implements OfferManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferManagerImpl.class);

    private static final String LINK_TEMPLATE = "%1$s/offers/redemption/link/%2$s?user=%3$s&offer_id=%4$d";

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

    @Autowired
    private RedemptionLinkProperties redemptionLinkProperties;

    @Autowired
    private GetVoucherRedirectHandler getVoucherRedirectHandler;

    @Value("${OFFER_SERVICE_URL:#{'http://localhost:8080'}}")
    private String offerServiceDomain;

    @Override
    public OfferDTO getOfferById(Long id) {
        return OfferMapper.fromOfferDBEntityToDTO(offerRepository.findOne(id));
    }

    @Override
    public OfferDTO createOffer(OfferDTO offerDTO) {
        OfferDBEntity offerDBEntity = offerOperationsRegistry.createOfferDBEntity(offerDTO);

        return OfferMapper.fromOfferDBEntityToDTO(offerRepository.save(offerDBEntity));
    }

    @Override
    public OfferDTO updateOffer(OfferDTO offerDTO, Long id) {
        OfferDBEntity oldOfferDBEntity = offerRepository.findOne(id);
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
            long now = jdbcHelper.lookupCurrentDbTime().getTime();
            long expiredOn = DateUtils.getCurrentTimeEndOfDay(now + redemptionLinkProperties.getMilliseconds());
            offerRedeemDBEntity.setUpdatedOn(now);
            offerRedeemDBEntity.setExpiredOn(expiredOn);
            offerRedeemDBEntity.setStatus(OfferRedeemStatusType.GENERATED);
            offerRedeemRepository.saveAndFlush(offerRedeemDBEntity);
        }
        return String.format(LINK_TEMPLATE, offerServiceDomain, offerRedeemDBEntity.getHash(), offerLinkGenerateDTO.getEmail(), offerLinkGenerateDTO.getOfferId());
    }

    @Override
    public void processRedemptionLinkRedirect(String hash, String email, Long id, HttpServletResponse response) {
        OfferRedeemDBEntity offerRedeemDBEntity = offerRedeemRepository.findByEmailAndOfferDBEntityIdAndHash(email, id, hash);

        if (offerRedeemDBEntity == null) {
            getVoucherRedirectHandler.processNotFoundVoucherRedirect(response);
        } else {
            long now = jdbcHelper.lookupCurrentDbTime().getTime();
            if (offerRedeemDBEntity.getExpiredOn() < now) {
                getVoucherRedirectHandler.processExpiredVoucherLinkRedirect(response, offerRedeemDBEntity.getExpiredOn());
            } else {
                if (!isRedemptionLinkClicked(offerRedeemDBEntity)) {
                    processRedemptionLinkClick(offerRedeemDBEntity, now);
                    //TODO: Add call to Amazon to generate voucher
                }
                //TODO: Fetch previously stored Amazon voucher information
                getVoucherRedirectHandler.processGetVoucherInfoRedirect(response, AmazonStubbedVoucher.EXPIRE_ON, AmazonStubbedVoucher.VOUCHER_CODE);
            }
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void processRedemptionLinkClick(OfferRedeemDBEntity offerRedeemDBEntity, Long now) {
        offerRedeemDBEntity.setStatus(OfferRedeemStatusType.CLICKED);
        offerRedeemDBEntity.setUpdatedOn(now);
        offerRedeemRepository.saveAndFlush(offerRedeemDBEntity);
        OfferDBEntity offerDBEntity = offerRedeemDBEntity.getOfferDBEntity();
        offerDBEntity.setLinksRedeemed(offerDBEntity.getLinksRedeemed() == null ? 1 : offerDBEntity.getLinksRedeemed() + 1);
        offerRepository.saveAndFlush(offerDBEntity);
    }

    private boolean isRedemptionLinkClicked(OfferRedeemDBEntity offerRedeemDBEntity) {
        return offerRedeemDBEntity.getOfferRedeemEventDBEntities().stream().anyMatch(or -> OfferRedeemStatusType.CLICKED.equals(or.getStatus()));
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
