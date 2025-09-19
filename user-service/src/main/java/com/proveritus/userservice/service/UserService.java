package com.proveritus.userservice.service;

import com.proveritus.cloudutility.dto.UserDTO;
import com.proveritus.userservice.DTO.SignUpRequest;
import com.proveritus.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link User}.
 */
public interface UserService {

    /**
     * Save a user.
     *
     * @param signUpRequest the entity to save.
     * @return the persisted entity.
     */
    UserDTO registerUser(SignUpRequest signUpRequest);

    /**
     * Get the current user.
     *
     * @return the current user.
     */
    UserDTO getCurrentUser();

    /**
     * Get all the users.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserDTO> getAllUsers(Pageable pageable);

    /**
     * Get the "id" user.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserDTO> getUserById(Long id);

    /**
     * Get the "username" user.
     *
     * @param username the username of the entity.
     * @return the entity.
     */
    Optional<UserDTO> getUserByUsername(String username);

    /**
     * Get users by a list of IDs.
     *
     * @param ids the list of user IDs.
     * @return a list of users.
     */
    List<UserDTO> getUsersByIds(List<Long> ids);

    /**
     * Delete the "id" user.
     *
     * @param id the id of the entity.
     */
    void deleteUser(Long id);

    /**
     * Create a new user.
     *
     * @param userDTO the entity to save.
     * @return the persisted entity.
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * Update a user.
     *
     * @param id the id of the entity.
     * @param userDTO the entity to update.
     * @return the persisted entity.
     */
    UserDTO updateUser(Long id, UserDTO userDTO);

    /**
     * Check if the current user is the given user.
     *
     * @param id the id of the entity.
     * @return true if the current user is the given user, false otherwise.
     */
    boolean isCurrentUser(Long id);
}
