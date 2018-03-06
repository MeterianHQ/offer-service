package com.ovoenergy.offer.rest;

import com.google.common.collect.ImmutableMap;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.ovoenergy.offer.dto.OffersServiceURLs.*;

@Api(value = "test-service")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private CustomValidationProcessor customValidator;

    @Autowired
    private OfferManager offerManager;

    @PostMapping(APPLY_TO_OFFER)
    @ResponseBody
    @ApiResponses({@ApiResponse(code = 200, message = "Ok", response = OfferVerifyDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = APPLY_TO_OFFER, notes = "Create offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferApplyDTO> applyToOfferOffer(@RequestBody OfferApplyDTO request) {
        LOGGER.debug("APPLY user to offer request has been received: {}", request);

        customValidator.processOfferInputDataValidationException(request);
        OfferApplyDTO response = offerManager.applyUserToOffer(request.getOfferCode(), request.getEmail());

        LOGGER.debug("Returning response for APPLY user to offer: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(GENERATE_LINK)
    @ResponseBody
    @ApiResponses({@ApiResponse(code = 201, message = "Created", response = OfferVerifyDTO.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessageDTO.class),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorMessageDTO.class)})
    @ApiOperation(value = GENERATE_LINK, notes = "Create offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> generateLink(@RequestBody OfferLinkGenerateDTO request) {
        LOGGER.debug("Generate link request has been received: {}", request);

        customValidator.processOfferInputDataValidationException(request);
        String link = offerManager.generateOfferLink(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ImmutableMap.of("link", link));
    }

}
