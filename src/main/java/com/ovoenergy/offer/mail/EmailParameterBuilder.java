package com.ovoenergy.offer.mail;

import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class EmailParameterBuilder {

    public static Map<String, Object> buildLinkEmailParams() {
        return ImmutableMap.<String, Object>builder()
                .put("voucherValue", 50)
                .put("voucherLinkExpiryDate", 1)
                .put("voucherLink", "generated_link")
                .build();
    }

    public static Map<String, Object> buildVoucherEmailParams() {
        return ImmutableMap.<String, Object>builder()
                .put("voucherCode", "CODE")
                .put("voucherExpiryDate", 1)
                .build();
    }
}
