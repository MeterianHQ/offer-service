package com.ovoenergy.offer.manager;

import com.ovoenergy.offer.dto.OfferDTO;

import java.util.List;

public interface OfferManager {

    OfferDTO getOfferById(Long id);

    OfferDTO createOffer(OfferDTO offerDTO);

    OfferDTO updateOffer(OfferDTO offerDTO, Long id);

    OfferDTO deleteOffer(Long id);

    List<OfferDTO> getAllOffers();

    Boolean validateOffer(String offerCode);

    Boolean applyToOffer(String offerCode, String emailAddress);

}
