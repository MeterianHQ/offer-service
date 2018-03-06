package com.ovoenergy.offer.amazon;

import java.time.LocalDateTime;
import java.time.ZoneId;

//TODO: Remove after real integration
public interface AmazonStubbedVoucher {

    public static final String VOUCHER_CODE = "AMAZON_STUB_03_2018";

    public static final Integer VOUCHER_VALUE = 50;

    public static final Long EXPIRE_ON = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

}
