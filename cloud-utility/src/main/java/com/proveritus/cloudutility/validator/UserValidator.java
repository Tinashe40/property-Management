package com.proveritus.cloudutility.validator;

import com.proveritus.cloudutility.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validate(UserDTO userDTO, boolean isUsernameTaken, boolean isEmailTaken) {
        if (isUsernameTaken) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        if (isEmailTaken) {
            throw new IllegalArgumentException("Email Address already in use!");
        }
    }
}
