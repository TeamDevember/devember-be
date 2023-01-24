package com.devember.devember.config.security.userdetail;

import com.devember.devember.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JwtUserDetails implements UserDetails, OAuth2User {

    private User user;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public JwtUserDetails(User user) {
        super();
        authorities = List.of(new SimpleGrantedAuthority(user.getRole().getValue()));
        this.user = user;
    }

    public static JwtUserDetails create(User user) {
        return new JwtUserDetails(user);
    }

    public static JwtUserDetails create(User user, Map<String, Object> attributes){
        JwtUserDetails jwtUserDetails = new JwtUserDetails(user);
        jwtUserDetails.setAttributes(attributes);

        return jwtUserDetails;
    }

    public String getUserId() {
        return user.getId().toString();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void setAttributes(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return user.getNickname();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
