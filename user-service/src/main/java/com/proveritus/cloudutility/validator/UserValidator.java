package com.proveritus.cloudutility.validator;

import com.proveritus.cloudutility.dto.UserDTO;
import com.proveritus.cloudutility.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {

    public void validate(UserDTO userDTO, boolean usernameExists, boolean emailExists) {
        List<String> errors = new ArrayList<>();

        if (usernameExists) {
            errors.add("Username is already taken");
        }

        if (emailExists) {
            errors.add("Email is already taken");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
