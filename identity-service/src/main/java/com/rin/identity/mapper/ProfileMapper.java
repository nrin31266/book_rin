package com.rin.identity.mapper;

import com.rin.identity.dto.request.ProfileCreationRequest;
import com.rin.identity.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
