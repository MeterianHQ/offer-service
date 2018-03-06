package com.ovoenergy.offer.manager;

import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferLinkGenerateDTO;

import javax.servlet.http.HttpServletResponse;
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

    void processRedemptionLinkRedirect(String hash, String email, Long id, HttpServletResponse response);
}
