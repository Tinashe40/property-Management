package com.proveritus.cloudutility.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomPrincipal extends User {

    private final Long id;

    public CustomPrincipal(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities,
                           boolean enabled, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }
}