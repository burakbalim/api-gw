package com.application.gateway.unit.main.middleware;

import com.application.gateway.main.middleware.UrlWriterService;
import com.application.gateway.main.middleware.model.UrlRewrites;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.*;

class UrlWriterServiceTest {

    private UrlWriterService urlWriterService = new UrlWriterService();

    @Test
    void isMatchingWithRule_1() {
        UrlRewrites urlRewrites = new UrlRewrites();
        urlRewrites.setPath("/commission");
        urlRewrites.setMethod(HttpMethod.GET.name());
        urlRewrites.setMatchPattern("/commission(.*)");
        urlRewrites.setRewriteTo("booking-service/en/v13/booking/reports/commission$1");

        String requestPath = "/gw/public/third-party/gw/public/third-party/booking/reporst/commission?month=12&year=2023";

        boolean matchingWithRule = urlWriterService.isMatchingWithRule(urlRewrites, requestPath, HttpMethod.GET.name());
        String rewroteUrl = urlWriterService.rewriteUrl(urlRewrites, requestPath);
        assertTrue(matchingWithRule);
        assertEquals("booking-service/en/v13/booking/reports/commission?month=12&year=2023", rewroteUrl);
    }

    @Test
    void isMatchingWithRule_2() {
        UrlRewrites urlRewrites = new UrlRewrites();
        urlRewrites.setPath("/create");
        urlRewrites.setMethod(HttpMethod.GET.name());
        urlRewrites.setMatchPattern("/create$");
        urlRewrites.setRewriteTo("price-service/en/v13/third-party/credit");

        String requestPath = "/gw/public/third-party/credit/create";

        boolean matchingWithRule = urlWriterService.isMatchingWithRule(urlRewrites, requestPath, HttpMethod.GET.name());
        String rewroteUrl = urlWriterService.rewriteUrl(urlRewrites, requestPath);
        assertTrue(matchingWithRule);
        assertEquals("price-service/en/v13/third-party/credit", rewroteUrl);
    }

    @Test
    void isMatchingWithRule_3() {
        UrlRewrites urlRewrites = new UrlRewrites();
        urlRewrites.setPath("/match/me");
        urlRewrites.setMethod(HttpMethod.GET.name());
        urlRewrites.setMatchPattern("(\\w+)/(\\w+)");
        urlRewrites.setRewriteTo("my/service?value1=$1&value2=$2");

        String requestPath = "/match/me";

        boolean matchingWithRule = urlWriterService.isMatchingWithRule(urlRewrites, requestPath, HttpMethod.GET.name());
        assertTrue(matchingWithRule);
        assertEquals("my/service?value1=match&value2=me", urlWriterService.rewriteUrl(urlRewrites, requestPath));
    }
}