package com.proveritus.propertyservice.service;

import com.proveritus.cloudutility.dto.UserDTO;
import com.proveritus.cloudutility.security.RemoteUserDetailsService;
import com.proveritus.propertyservice.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoteUserDetailsServiceImpl extends RemoteUserDetailsService {

    private final UserClient userClient;

    @Override
    public UserDTO getUserByUsername(String username) {
        return userClient.getUserByUsername(username);
    }
}
