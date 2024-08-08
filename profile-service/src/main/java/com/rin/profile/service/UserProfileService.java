package com.rin.profile.service;

import com.rin.profile.dto.request.ProfileCreationRequest;
import com.rin.profile.dto.response.UserProfileResponse;
import com.rin.profile.entity.UserProfile;
import com.rin.profile.mapper.UserProfileMapper;
import com.rin.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        userProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public UserProfileResponse getUserProfile(String id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public List<UserProfileResponse> getAllUserProfiles() {
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        return userProfiles.stream().map(userProfileMapper::toUserProfileReponse).toList();
    }

}
