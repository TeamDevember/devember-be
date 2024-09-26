package com.gridians.gridians.global.config.security.filter;

import com.gridians.gridians.global.config.security.service.CustomUserDetailsService;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean urlCheck = true;
        List<RequestMatcher> requestMatchers = MatcherFactory.getMatcher();
        for(RequestMatcher requestMatcher : requestMatchers) {
            if(requestMatcher.matches(request)) {
                urlCheck = false;
            }
        }

        if (urlCheck) {
            try {
                String jwt = getResolveAuthHeader(request);
                log.info("jwt = {}", jwt);
                if (StringUtils.hasText(jwt) && jwtUtils.validateToken(jwt)) {
                    String email = jwtUtils.getUserEmailFromToken(jwt);

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
//            } catch (ExpiredJwtException exception) {
//                throw exception;
//            } catch (JwtException exception) {
//                throw exception;
            } catch (Exception exception) {
                request.getRequestURI();
                exception.printStackTrace();
                throw exception;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getResolveAuthHeader(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        log.info("auth = {}", auth);
        if(StringUtils.hasText(auth) && auth.startsWith("Bearer")){
            return auth.substring(7);
        }
        throw new RuntimeException("not found authorization value");
    }
}
