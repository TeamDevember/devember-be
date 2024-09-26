package com.gridians.gridians.global.config.security.service;


import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
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
