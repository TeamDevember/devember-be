package com.gridians.gridians.domain.user;

import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(annotation.role()));
        UUID uuid = UUID.randomUUID();
        User user = User.builder()
                .id(uuid)
                .email(annotation.username())
                .nickname("nickname")
                .password("password12!")
                .role(Role.valueOf(annotation.role()))
                .userStatus(UserStatus.ACTIVE)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(JwtUserDetails.create(user), null, grantedAuthorities);
        authentication.setDetails(JwtUserDetails.create(user));
        context.setAuthentication(authentication);
        return context;
    }
}
