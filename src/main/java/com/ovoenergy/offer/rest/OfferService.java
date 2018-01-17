package com.ovoenergy.offer.rest;

import com.google.common.collect.Lists;
import com.ovoenergy.offer.dto.OfferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ovoenergy.offer.dto.OffersServiceURLs.*;

@RestController
@Validated
public class OfferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferService.class);

    @RequestMapping(value = GET_OFFER, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDTO> getOfferById(@RequestParam String id) {
        LOGGER.debug("GET offer by id request has been received: {}", id);
        //TODO: Add business logic for get by id
        OfferDTO response = new OfferDTO();
        LOGGER.debug("Returning response for GET offer by id: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = GET_ALL_OFFERS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OfferDTO>> getAllOffers() {
        LOGGER.debug("GET all offers");
        //TODO: Add business logic for get all offers
        List<OfferDTO> response = Lists.newArrayList(new OfferDTO());
        LOGGER.debug("Returning response for GET all offers");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = CREATE_OFFER, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDTO> craeteOffer(@RequestBody OfferDTO request) {
        LOGGER.debug("CREATE offer request has been received: {}", request);
        //TODO: Add business logic for create
        OfferDTO response = new OfferDTO();
        LOGGER.debug("Returning response for CREATE offer: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = UPDATE_OFFER, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDTO> updateOffer(@RequestParam String id, @RequestBody OfferDTO request) {
        LOGGER.debug("UPDATE offer with id = {} request has been received: {}", id, request);
        //TODO: Add business logic for update
        OfferDTO response = new OfferDTO();
        LOGGER.debug("Returning response for UPDATE offer with id = {}: {}", id, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = DELETE_OFFER, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDTO> deleteOffer(@RequestParam String id) {
        LOGGER.debug("DELETE offer by id = {} request has been received", id);
        //TODO: Add business logic for delete
        OfferDTO response = new OfferDTO();
        LOGGER.debug("Returning response for DELETE offer by id {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}