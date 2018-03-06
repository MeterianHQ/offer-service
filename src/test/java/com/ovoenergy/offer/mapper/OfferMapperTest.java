package com.ovoenergy.offer.mapper;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.google.common.primitives.Longs;
import com.ovoenergy.offer.db.entity.ChannelType;
import com.ovoenergy.offer.db.entity.EligibilityCriteriaType;
import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.OfferType;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.entity.SupplierType;
import com.ovoenergy.offer.dto.OfferDTO;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OfferMapperTest {

    @Rule
    public FixtureRule fixtures = FixtureRule.initFixtures();

    @Fixture
    private OfferDBEntity fixtureOfferDBEntity;

    @Fixture
    private OfferDTO fixtureOfferDTO;

    @Test
    public void testFomOfferDBEntityToDTOSuccess() {
        OfferDTO result = OfferMapper.fromOfferDBEntityToDTO(fixtureOfferDBEntity);

        assertEquals(fixtureOfferDBEntity.getOfferCode(), result.getOfferCode());
        assertEquals(fixtureOfferDBEntity.getDescription(), result.getDescription());
        assertEquals(fixtureOfferDBEntity.getOfferName(), result.getOfferName());
        assertEquals(fixtureOfferDBEntity.getChannel().value(), result.getChannel());
        assertEquals(fixtureOfferDBEntity.getActualOfferRedemptions(), result.getActualOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getMaxOfferRedemptions().toString(), result.getMaxOfferRedemptions());
        assertEquals(fixtureOfferDBEntity.getExpiryDate(), result.getExpiryDate());
        assertEquals(fixtureOfferDBEntity.getStartDate(), result.getStartDate());
        assertEquals(fixtureOfferDBEntity.getIsExpirable(), result.getIsExpirable());
        assertEquals(fixtureOfferDBEntity.getEligibilityCriteria().value(), result.getEligibilityCriteria());
        assertEquals(fixtureOfferDBEntity.getOfferType().value(), result.getOfferType());
        assertEquals(fixtureOfferDBEntity.getStatus().name(), result.getStatus());
        assertEquals(fixtureOfferDBEntity.getSupplier().value(), result.getSupplier());
        assertEquals(fixtureOfferDBEntity.getUpdatedOn(), result.getUpdatedOn());
        assertEquals(fixtureOfferDBEntity.getValue().toString(), result.getValue());
        assertEquals(fixtureOfferDBEntity.getLinksRedeemed(), result.getLinksRedeemed());
        assertEquals(fixtureOfferDBEntity.getId(), result.getId());
    }

    @Test
    public void testFromOfferDTOTODBEntitySuccess() {
        fixtureOfferDTO.setChannel(ChannelType.EMAIL.value());
        fixtureOfferDTO.setEligibilityCriteria(EligibilityCriteriaType.SSD.value());
        fixtureOfferDTO.setOfferType(OfferType.GIFTCARD.value());
        fixtureOfferDTO.setStatus(StatusType.ACTIVE.name());
        fixtureOfferDTO.setSupplier(SupplierType.AMAZON.value());

        OfferDBEntity result = OfferMapper.fromOfferDTOTODBEntity(fixtureOfferDTO);

        assertEquals(fixtureOfferDTO.getOfferCode(), result.getOfferCode());
        assertEquals(fixtureOfferDTO.getDescription(), result.getDescription());
        assertEquals(fixtureOfferDTO.getOfferName(), result.getOfferName());
        assertEquals(fixtureOfferDTO.getChannel(), result.getChannel().value());
        assertEquals(Longs.tryParse(fixtureOfferDTO.getMaxOfferRedemptions()), result.getMaxOfferRedemptions());
        assertEquals(fixtureOfferDTO.getExpiryDate(), result.getExpiryDate());
        assertEquals(fixtureOfferDTO.getStartDate(), result.getStartDate());
        assertEquals(fixtureOfferDTO.getIsExpirable(), result.getIsExpirable());
        assertEquals(fixtureOfferDTO.getEligibilityCriteria(), result.getEligibilityCriteria().value());
        assertEquals(fixtureOfferDTO.getOfferType(), result.getOfferType().value());
        assertEquals(fixtureOfferDTO.getSupplier(), result.getSupplier().value());
        assertEquals(Longs.tryParse(fixtureOfferDTO.getValue()), result.getValue());
        assertEquals(fixtureOfferDTO.getId(), result.getId());
        assertEquals(fixtureOfferDTO.getLinksRedeemed(), result.getLinksRedeemed());
    }
}
