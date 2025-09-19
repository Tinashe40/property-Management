package com.proveritus.userservice.repository;


import com.proveritus.cloudutility.jpa.BaseDao;
import com.proveritus.userservice.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseDao<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
