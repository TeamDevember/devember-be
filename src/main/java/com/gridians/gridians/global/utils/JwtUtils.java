package com.gridians.gridians.global.utils;

import com.gridians.gridians.global.config.security.service.CustomUserDetailsService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.domain.user.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    public final Long ACCESS_TOKEN_EXPIRE_TIME;
    public final Long REFRESH_TOKEN_EXPIRE_TIME;

    private Key key;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TokenRepository tokenRepository;

    public JwtUtils(
            @Value("${jwt.access-token-expire-time}") Long accessTime,
            @Value("${jwt.refresh-token-expire-time}") Long refreshTime,
            @Value("${jwt.secret}") String secretKey
    ) {
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(Authentication authentication, long expireTime) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().toString();

        Claims claims = Jwts.claims();
        claims.setSubject(userDetails.getUserId());
        claims.put("email", userDetails.getEmail());
        claims.put("role", role);
        claims.put("name", userDetails.getName());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("email");
    }

    public String createAccessToken(Authentication authentication){
        return createToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public Authentication getAuthenticationByToken(String token) {
        String email = getUserEmailFromToken(token);
        return getAuthenticationByEmail(email);
    }

    public Authentication getAuthenticationByEmail(String email){
        JwtUserDetails jwtUserDetails = (JwtUserDetails) customUserDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(jwtUserDetails, null, jwtUserDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            if(tokenRepository.hasKeyBlackList(token)){
                throw new RuntimeException("이미 탈퇴한 회원 입니다.");
            }
            return true;
        } catch (ExpiredJwtException exception) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
}
