package com.gridians.gridians.global.config.security.filter;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

public class MatcherFactory {

    public static String[] permitUrls = {"/user/auth/**", "/cards", "/image/**", "/profile-images/**", "/skill-images/**"};
    public static List<RequestMatcher> getMatcher() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        for(String permitUrl : permitUrls) {
            requestMatchers.add(new AntPathRequestMatcher(permitUrl));
        }

        return requestMatchers;
    }

}
