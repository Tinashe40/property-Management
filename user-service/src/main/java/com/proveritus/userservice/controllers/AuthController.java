package com.proveritus.userservice.controllers;

import com.proveritus.cloudutility.dto.UserDTO;
import com.proveritus.userservice.DTO.JwtAuthResponse;
import com.proveritus.userservice.DTO.LoginRequest;
import com.proveritus.userservice.DTO.SignUpRequest;
import com.proveritus.cloudutility.security.JwtTokenProvider;
import com.proveritus.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/sign-in")
    @Operation(summary = "Authenticates a user and returns a JWT token")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthResponse(jwt));
    }

    @PostMapping("/signup")
    @Operation(summary = "Registers a new user")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        UserDTO result = userService.registerUser(signUpRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{id}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).body(result);
    }

    @GetMapping("/me")
    @Operation(summary = "Gets the current user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
}
