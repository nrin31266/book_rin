package com.rin.profile.mapper;

import org.mapstruct.Mapper;

import com.rin.profile.dto.request.ProfileCreationRequest;
import com.rin.profile.dto.response.UserProfileResponse;
import com.rin.profile.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileReponse(UserProfile entity);
}