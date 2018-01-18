package com.ovoenergy.offer.rest;

import com.google.common.collect.Lists;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ovoenergy.offer.dto.OffersServiceURLs.*;

@Api(value = "offer-service")
@RestController
public class OfferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferService.class);

    @RequestMapping(value = GET_OFFER, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = OfferDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class), })
    @ApiOperation(value = GET_OFFER, notes = "Get offer by id", produces = "application/json")
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable String id) {
        LOGGER.debug("GET offer by id request has been received: {}", id);
        //TODO: Add business logic for get by id
        OfferDTO response = new OfferDTO();
        LOGGER.debug("Returning response for GET offer by id: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = GET_ALL_OFFERS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = OfferDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class), })
    @ApiOperation(value = GET_ALL_OFFERS, notes = "Get all offers", produces = "application/json")
    public ResponseEntity<List<OfferDTO>> getAllOffers() {
        LOGGER.debug("GET all offers");
        //TODO: Add business logic for get all offers
        List<OfferDTO> response = Lists.newArrayList(new OfferDTO());
        LOGGER.debug("Returning response for GET all offers");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = CREATE_OFFER, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = OfferDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class), })
    @ApiOperation(value = CREATE_OFFER, notes = "Create offer", produces = "application/json")
    public ResponseEntity<OfferDTO> craeteOffer(@RequestBody(required = true) OfferDTO request) {
        LOGGER.debug("CREATE offer request has been received: {}", request);
        //TODO: Add business logic for create
        OfferDTO response = new OfferDTO();
        LOGGER.debug("Returning response for CREATE offer: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = UPDATE_OFFER, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = OfferDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class), })
    @ApiOperation(value = UPDATE_OFFER, notes = "Update offer", produces = "application/json")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable String id, @RequestBody(required = true) OfferDTO request) {
        LOGGER.debug("UPDATE offer with id = {} request has been received: {}", id, request);
        //TODO: Add business logic for update
        OfferDTO response = new OfferDTO();
        LOGGER.debug("Returning response for UPDATE offer with id = {}: {}", id, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = DELETE_OFFER, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = OfferDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class), })
    @ApiOperation(value = DELETE_OFFER, notes = "Delete offer", produces = "application/json")
    public ResponseEntity<OfferDTO> deleteOffer(@PathVariable String id) {
        LOGGER.debug("DELETE offer by id = {} request has been received", id);
        //TODO: Add business logic for delete
        OfferDTO response = new OfferDTO();
        LOGGER.debug("Returning response for DELETE offer by id {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}