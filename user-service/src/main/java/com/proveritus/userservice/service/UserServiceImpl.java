package com.proveritus.userservice.service;

import com.proveritus.cloudutility.dto.UserDTO;
import com.proveritus.cloudutility.enums.UserRole;
import com.proveritus.userservice.DTO.SignUpRequest;
import com.proveritus.userservice.entity.User;
import com.proveritus.cloudutility.exception.UserNotFoundException;
import com.proveritus.userservice.mapper.UserMapper;
import com.proveritus.userservice.repository.UserRepository;
import com.proveritus.cloudutility.security.CustomPrincipal;
import com.proveritus.cloudutility.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link User}.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Override
    public UserDTO registerUser(SignUpRequest signUpRequest) {
        log.debug("Request to register user : {}", signUpRequest.getUsername());

        userValidator.validate(userMapper.toDto(signUpRequest), userRepository.existsByUsername(signUpRequest.getUsername()), userRepository.existsByEmail(signUpRequest.getEmail()));

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        UserRole role = Optional.ofNullable(signUpRequest.getRole())
                .map(String::toUpperCase)
                .map(UserRole::valueOf)
                .orElse(UserRole.VIEWER);
        user.setRole(role);

        User result = userRepository.save(user);
        log.debug("Saved user : {}", result);

        return userMapper.toDto(result);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        CustomPrincipal userPrincipal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userPrincipal.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.debug("Request to get all Users");
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public Optional<UserDTO> getUserById(Long id) {
        log.debug("Request to get User : {}", id);
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#username")
    public Optional<UserDTO> getUserByUsername(String username) {
        log.debug("Request to get User : {}", username);
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByIds(List<Long> ids) {
        log.debug("Request to get Users by IDs: {}", ids);
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.debug("Request to delete User : {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        log.debug("Deleted User: {}", user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCurrentUser(Long id) {
        CustomPrincipal userPrincipal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getId().equals(id);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO createUser(UserDTO userDTO) {
        log.debug("Request to create User : {}", userDTO.getUsername());

        userValidator.validate(userDTO, userRepository.existsByUsername(userDTO.getUsername()), userRepository.existsByEmail(userDTO.getEmail()));

        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);
        log.debug("Saved user : {}", result);

        return userMapper.toDto(result);
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.debug("Request to update User : {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setRole(userDTO.getRole());
        user.setEnabled(userDTO.isEnabled());
        user.setAccountNonExpired(userDTO.isAccountNonExpired());
        user.setAccountNonLocked(userDTO.isAccountNonLocked());
        user.setCredentialsNonExpired(userDTO.isCredentialsNonExpired());

        User result = userRepository.save(user);
        log.debug("Updated user : {}", result);

        return userMapper.toDto(result);
    }
}