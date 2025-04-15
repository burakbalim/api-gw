package com.application.gateway.main.middleware;

import com.application.gateway.main.middleware.model.UrlRewrites;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlWriterService {

    private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\$(\\d+)");
    private static final String PREFIX_WILD_CARD = "\\$";

    public boolean isMatchingWithRule(UrlRewrites urlRewriter, String requestUrl, String requestMethod) {
        return requestMethod.equals(urlRewriter.getMethod()) && requestUrl.contains(urlRewriter.getPath());
    }

    public String rewriteUrl(UrlRewrites urlRewriter, String requestUrl) {
        Matcher templateMatcher = getMatcher(urlRewriter.getMatchPattern(), requestUrl);
        Matcher rewriteMatcher = WILDCARD_PATTERN.matcher(urlRewriter.getRewriteTo());
        String newPath = urlRewriter.getRewriteTo();
        if (templateMatcher.find()) {
            while (rewriteMatcher.find()) {
                String wildCardNumber = rewriteMatcher.group(1);
                Matcher matcher1 = getMatcher(PREFIX_WILD_CARD + wildCardNumber, newPath);
                newPath = matcher1.replaceAll(templateMatcher.group(Integer.parseInt(wildCardNumber)));
            }
        }
        return newPath;
    }

    private static Matcher getMatcher(String regex, String input) {
        Pattern templatePattern = Pattern.compile(regex, Pattern.DOTALL);
        return templatePattern.matcher(input);
    }
}
