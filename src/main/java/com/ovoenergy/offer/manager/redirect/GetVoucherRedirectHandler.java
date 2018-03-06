package com.ovoenergy.offer.manager.redirect;

import com.google.common.collect.ImmutableMap;
import com.ovoenergy.offer.exception.VoucherRedirectOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

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
        MultiValueMap queryParams = new HttpHeaders();
        queryParams.setAll(ImmutableMap.of(EXPIRE_ON_DATE_QUERY_PARAM, expireOnDate.toString(), VOUCHER_CODE_QUERY_PARAM, voucherCode));
        String location = resolveRedirectUrl(voucherInfoPage, queryParams);
        processRedirect(location, response);
    }

    public void processExpiredVoucherLinkRedirect(HttpServletResponse response, Long expireOnDate) {
        MultiValueMap queryParams = new HttpHeaders();
        queryParams.setAll(ImmutableMap.of(EXPIRE_ON_DATE_QUERY_PARAM, expireOnDate.toString()));
        String location = resolveRedirectUrl(voucherExpiredPage, queryParams);
        processRedirect(location, response);
    }

    public void processNotFoundVoucherRedirect(HttpServletResponse response) {
        String location = resolveRedirectUrl(voucherNotFoundPage, null);
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

    private String resolveRedirectUrl(String page, MultiValueMap<String, String> queryArgs) {
        UriComponents uriComponents = UriComponentsBuilder
                .newInstance()
                .path(voucherPublicURL)
                .pathSegment(page)
                .queryParams(queryArgs)
                .build();

        return uriComponents.toUriString();
    }

}
