package com.ovoenergy.offer.integration;

import com.ovoenergy.offer.Application;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OffersServiceURLs;
import com.ovoenergy.offer.test.utils.IntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@IntegrationTest
/*
@TestPropertySource(properties = {"management.port=0"})
*/
public class OfferServiceIntegrationTest {

    private final static String TEST_VALID_DESCRIPTION = "description";
    private final static String TEST_VALID_NAME = "name";
    private final static String TEST_VALID_CODE = "code";
    private final static String TEST_VALID_SUPPLIER = "Amazon";
    private final static String TEST_VALID_OFFER_TYPE = "Giftcard";
    private final static String TEST_VALID_ELIGIBILITY_CRITERIA = "SSD";
    private final static String TEST_VALID_CHANEL = "Email";
    private static final Long TEST_VALID_START_DATE = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() ;

    @LocalServerPort
    private int port;

    private  TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    public void testValidateOfferForCreate() {
        OfferDTO offerToValidate = new OfferDTO();
        offerToValidate.setDescription(TEST_VALID_DESCRIPTION);
        offerToValidate.setOfferName(TEST_VALID_NAME);
        offerToValidate.setOfferCode(TEST_VALID_CODE);
        offerToValidate.setSupplier(TEST_VALID_SUPPLIER);
        offerToValidate.setOfferType(TEST_VALID_OFFER_TYPE);
        offerToValidate.setValue(3L);
        offerToValidate.setMaxOfferRedemptions(3L);
        offerToValidate.setEligibilityCriteria(TEST_VALID_ELIGIBILITY_CRITERIA);
        offerToValidate.setChannel(TEST_VALID_CHANEL);
        offerToValidate.setStartDate(TEST_VALID_START_DATE);
        offerToValidate.setExpiryDate(TEST_VALID_START_DATE);
        offerToValidate.setIsExpirable(true);

        HttpEntity<OfferDTO> entity = new HttpEntity<OfferDTO>(offerToValidate, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(OffersServiceURLs.CREATE_OFFER),
                HttpMethod.POST, entity, String.class);

        System.out.println("Resp " + response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //ADD asserts for all post conditions
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
