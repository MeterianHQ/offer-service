package com.ovoenergy.offer.manager;

import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferLinkGenerateDTO;
import com.ovoenergy.offer.dto.OfferRedeemInfoDTO;

import java.util.List;

public interface OfferManager {

    OfferDTO getOfferById(Long id);

    OfferDTO createOffer(OfferDTO offerDTO);

    OfferDTO updateOffer(OfferDTO offerDTO, Long id);

    OfferDTO deleteOffer(Long id);

    List<OfferDTO> getAllOffers();

    Boolean verifyOffer(String offerCode);

    OfferApplyDTO applyUserToOffer(String offerCode, String emailAddress);

    String generateOfferLink(OfferLinkGenerateDTO offerLinkGenerateDTO);

    OfferRedeemInfoDTO getOfferRedeemInfo(String hash, String email, Long id);
}
