package com.ovoenergy.offer.rest;

import com.ovoenergy.offer.dto.*;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.validation.CustomValidationProcessor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.ovoenergy.offer.dto.OffersServiceURLs.*;

@Api(value = "offer-service")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class OfferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferService.class);

    @Autowired
    private CustomValidationProcessor customValidator;

    @Autowired
    private OfferManager offerManager;

    @GetMapping(GET_OFFER)
    @ResponseBody
    @ApiResponses({@ApiResponse(code = 200, message = "Ok", response = OfferDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = GET_OFFER, notes = "Get offer by id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable Long id) {
        LOGGER.debug("GET offer by id request has been received: {}", id);

        OfferDTO response = offerManager.getOfferById(id);

        LOGGER.debug("Returning response for GET offer by id: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(GET_ALL_OFFERS)
    @ResponseBody
    @ApiResponses({@ApiResponse(code = 200, message = "Ok", response = OfferDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = GET_ALL_OFFERS, notes = "Get all offers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OfferDTO>> getAllOffers() {
        LOGGER.debug("GET all offers");

        List<OfferDTO> response = offerManager.getAllOffers();

        LOGGER.debug("Returning response for GET all offers");
        return ResponseEntity.ok(response);
    }

    @PostMapping(CREATE_OFFER)
    @ResponseBody
    @ApiResponses({@ApiResponse(code = 200, message = "Ok", response = OfferDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = CREATE_OFFER, notes = "Create offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDTO> createOffer(@RequestBody OfferDTO request) {
        LOGGER.debug("CREATE offer request has been received: {}", request);

        OfferValidationDTO validationDTO = customValidator.processOfferCreateValidation(request);

        if (validationDTO != null) {
            return ResponseEntity.badRequest().body(validationDTO);
        }

        OfferDTO response = offerManager.createOffer(request);

        if (response instanceof OfferValidationDTO) {
            return ResponseEntity.badRequest().body(response);
        }

        LOGGER.debug("Returning response for CREATE offer: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping(UPDATE_OFFER)
    @ResponseBody
    @ApiResponses({@ApiResponse(code = 200, message = "Ok", response = OfferDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = UPDATE_OFFER, notes = "Update offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable Long id, @RequestBody OfferDTO request) {
        LOGGER.debug("UPDATE offer with id = {} request has been received: {}", id, request);

        OfferValidationDTO validationDTO = customValidator.processOfferUpdateValidation(request, id);
        if (validationDTO != null) {
            return ResponseEntity.badRequest().body(validationDTO);
        }

        OfferDTO response = offerManager.updateOffer(request, id);

        if (response instanceof OfferValidationDTO) {
            return ResponseEntity.badRequest().body(response);
        }

        LOGGER.debug("Returning response for UPDATE offer with id = {}: {}", id, response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(DELETE_OFFER)
    @ResponseBody
    @ApiResponses({@ApiResponse(code = 200, message = "Ok", response = OfferDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = DELETE_OFFER, notes = "Delete offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDTO> deleteOffer(@PathVariable Long id) {
        LOGGER.debug("DELETE offer by id = {} request has been received", id);

        OfferDTO response = offerManager.deleteOffer(id);

        LOGGER.debug("Returning response for DELETE offer by id {}", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(VERIFY_OFFER)
    @ResponseBody
    @ApiResponses({@ApiResponse(code = 200, message = "Ok", response = OfferVerifyDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = VERIFY_OFFER, notes = "Create offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> verifyOffer(@RequestBody OfferVerifyDTO request) {
        LOGGER.debug("VERIFY offer request has been received: {}", request);

        customValidator.processOfferInputDataInvalidOfferException(request);
        Boolean response = offerManager.verifyOffer(request.getOfferCode());

        LOGGER.debug("Returning response for VERIFY offer: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = CHECK_LINK, params = {"user", "offer_id"})
    @ApiResponses({ @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = CHECK_LINK, notes = "Redirects to voucher info url")
    public void getRedemptionLinkInfo(@PathVariable("hash") String hash,
                                      @RequestParam("user") String user,
                                      @RequestParam("offer_id") Long offerId,
                                      HttpServletResponse response) {
        offerManager.processRedemptionLinkRedirect(hash, user, offerId, response);
    }

}