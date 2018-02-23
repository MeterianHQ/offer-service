package com.ovoenergy.offer.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class OfferRedeemInfoDTO {
    private String email;
    private Long updatedOn;
    private String offerCode;
    private String offerName;
}
