package com.proveritus.cloudutility.security;

import com.proveritus.cloudutility.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public abstract class RemoteUserDetailsService implements UserDetailsService {

    public abstract UserDTO getUserByUsername(String username);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO = getUserByUsername(username);

        if (userDTO == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return createPrincipal(userDTO);
    }

    private CustomPrincipal createPrincipal(UserDTO user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new CustomPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "", // Password is not needed in this context
                authorities,
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired()
        );
    }
}