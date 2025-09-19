package com.proveritus.userservice.service;

import com.proveritus.cloudutility.security.CustomPrincipal;
import com.proveritus.userservice.entity.User;
import com.proveritus.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Loading user by username or email: {}", usernameOrEmail);

        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> {
                            String errorMsg = "User not found with username or email: " + usernameOrEmail;
                            log.error(errorMsg);
                            return new UsernameNotFoundException(errorMsg);
                        }));

        return createPrincipal(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        log.debug("Loading user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMsg = "User not found with id: " + id;
                    log.error(errorMsg);
                    return new UsernameNotFoundException(errorMsg);
                });

        return createPrincipal(user);
    }

    private CustomPrincipal createPrincipal(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new CustomPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired()
        );
    }
}
