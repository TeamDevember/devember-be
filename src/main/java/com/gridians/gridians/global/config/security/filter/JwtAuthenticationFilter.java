package com.gridians.gridians.global.config.security.filter;

import com.gridians.gridians.global.config.security.service.CustomUserDetailsService;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    private String[] permitUrl = {"/user/auth/**", "/cards", "/image/**"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean needTokenUrl = true;
        for(String matcher : permitUrl) {
            if(matcher.matches(request.getRequestURI())) {
                needTokenUrl = false;
            }
        }

        if (needTokenUrl) {
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
