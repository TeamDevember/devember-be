package com.devember.global.config.security.service;

import com.polymorph.pipeline.config.security.userdetail.JwtUserDetails;
import com.polymorph.pipeline.user.entity.User;
import com.polymorph.pipeline.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found User"));

        return JwtUserDetails.create(user);
    }
}
