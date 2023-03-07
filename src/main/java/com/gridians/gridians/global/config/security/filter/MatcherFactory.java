package com.gridians.gridians.global.config.security.filter;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

public class MatcherFactory {

    public static String[] permitUrlsMethodAll = {"/user/auth/**", "/profile-images/**", "/skill-images/**"};
    public static String[] permitUrlsMethodGet = {"/cards", "/cards/*", "/cards/*/comments", "/cards/*/comments/*/replies"};
    public static List<RequestMatcher> getMatcher() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        for(String permitUrl : permitUrlsMethodAll) {
            requestMatchers.add(new AntPathRequestMatcher(permitUrl));
        }

        for(String permitUrl : permitUrlsMethodGet) {
            requestMatchers.add(new AntPathRequestMatcher(permitUrl, "GET"));
        }

        return requestMatchers;
    }

    public static String[] hasRoleUrlsMethodGet = {"/cards/my-card", "/favorite"};

    public static List<RequestMatcher> getHasRole() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        for(String hasRoleUrl : hasRoleUrlsMethodGet) {
            requestMatchers.add(new AntPathRequestMatcher(hasRoleUrl));
        }

        return requestMatchers;
    }




}
