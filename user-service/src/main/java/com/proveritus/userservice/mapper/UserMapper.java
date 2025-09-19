package com.proveritus.userservice.mapper;

import com.proveritus.cloudutility.dto.UserDTO;
import com.proveritus.cloudutility.mapper.EntityMapper;
import com.proveritus.userservice.DTO.SignUpRequest;
import com.proveritus.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import org.mapstruct.MappingTarget;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDTO, User> {

        UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    UserDTO toDto(SignUpRequest signUpRequest);

    
}