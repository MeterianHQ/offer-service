package com.ovoenergy.offer.rest;

import com.flextrade.jfixture.rules.FixtureRule;
import com.ovoenergy.offer.dto.OfferApplyDTO;
import com.ovoenergy.offer.manager.OfferManager;
import com.ovoenergy.offer.validation.CustomValidationProcessor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestServiceTest {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_OFFER_CODE = "testOfferCode";

    @InjectMocks
    private TestService unit;

    @Mock
    private CustomValidationProcessor mockCustomValidator;

    @Mock
    private OfferManager mockOfferManager;

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();

    @Test
    public void testApplyOfferSuccess() {
        OfferApplyDTO offerApplyDTO = new OfferApplyDTO();
        offerApplyDTO.setOfferCode(TEST_OFFER_CODE);
        offerApplyDTO.setEmail(TEST_EMAIL);

        when(mockOfferManager.applyUserToOffer(eq(offerApplyDTO.getOfferCode()), eq(offerApplyDTO.getEmail()))).thenReturn(offerApplyDTO);

        ResponseEntity<OfferApplyDTO> result = unit.applyToOfferOffer(offerApplyDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(TEST_EMAIL, result.getBody().getEmail());
        assertEquals(TEST_OFFER_CODE, result.getBody().getOfferCode());
        verify(mockOfferManager).applyUserToOffer(eq(offerApplyDTO.getOfferCode()), eq(offerApplyDTO.getEmail()));
    }
}