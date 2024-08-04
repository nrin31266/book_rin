package com.rin.identity.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.rin.identity.dto.request.UserCreationRequest;
import com.rin.identity.dto.request.UserUpdateRequest;
import com.rin.identity.dto.response.UserResponse;
import com.rin.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    //    @Mapping(source = "firstName", target = "lastName")
    //    @Mapping(target = "lastName", ignore = true) //Không mapping
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void userUpdate(@MappingTarget User user, UserUpdateRequest request);

    List<UserResponse> toUserResponse(List<User> users);
}
