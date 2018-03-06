package com.ovoenergy.offer.manager.redirect;

import com.google.common.collect.ImmutableMap;
import com.ovoenergy.offer.exception.VoucherRedirectOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

@Component
public class GetVoucherRedirectHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetVoucherRedirectHandler.class);

    private static final String EXPIRE_ON_DATE_QUERY_PARAM = "expire-on-date";
    private static final String VOUCHER_CODE_QUERY_PARAM = "voucher-code";

    @Value("${voucher.ui.public.url}")
    private String voucherPublicURL;

    @Value("${voucher.ui.info.page}")
    private String voucherInfoPage;


    @Value("${voucher.ui.expired.page}")
    private String voucherExpiredPage;

    @Value("${voucher.ui.not.found.page}")
    private String voucherNotFoundPage;

    public void processGetVoucherInfoRedirect(HttpServletResponse response, Long expireOnDate, String voucherCode) {
        String location = resolveRedirectVoucherInfoUrl(voucherExpiredPage, ImmutableMap.of(EXPIRE_ON_DATE_QUERY_PARAM, expireOnDate.toString(), VOUCHER_CODE_QUERY_PARAM, voucherCode));
        processRedirect(location, response);
    }

    public void processExpiredVoucherLinkRedirect(HttpServletResponse response, Long expireOnDate) {
        String location = resolveRedirectVoucherInfoUrl(voucherExpiredPage, ImmutableMap.of(EXPIRE_ON_DATE_QUERY_PARAM, expireOnDate.toString()));
        processRedirect(location, response);
    }

    public void processNotFoundVoucherRedirect(HttpServletResponse response) {
        String location = resolveRedirectVoucherInfoUrl(voucherNotFoundPage, ImmutableMap.of());
        processRedirect(location, response);
    }

    private void processRedirect(String location, HttpServletResponse response) {
        try {
            response.sendRedirect(location);
        }
        catch (Exception e) {
            LOGGER.error("Failed to redirect user to a location = {}", location);
            throw new VoucherRedirectOperationException("Failed to redirect user to a location = " + location, e);
        }
    }

    private String resolveRedirectVoucherInfoUrl(String page, Map<String, String> queryArgs) {
        StringBuilder builder =  new StringBuilder(voucherPublicURL);
        builder.append(page);

        Set<String> keys = queryArgs.keySet();
        if (keys.size() > 0) {
            builder.append("?");
            keys.stream().forEach(key -> builder.append(queryArgs.get(key)).append("&"));
        }

        return builder.replace(builder.lastIndexOf("&"), builder.lastIndexOf("&"), "").toString();
    }

}
