package com.ovoenergy.offer.dto;

public interface OffersServiceURLs {

    String GET_OFFER = "/offers/{id}";

    String CREATE_OFFER = "/offers";

    String UPDATE_OFFER = "/offers/{id}";

    String DELETE_OFFER = "/offers/{id}";

    String GET_ALL_OFFERS = "/offers";

    String VERIFY_OFFER = "/offers/verify";

    String APPLY_TO_OFFER = "/offers/apply";

    String GENERATE_LINK = "/offers/test/link/generation";

    String CHECK_LINK = "/offers/redemption/link/{hash}";
}
