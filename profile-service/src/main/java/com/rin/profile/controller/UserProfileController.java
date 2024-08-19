package com.rin.profile.controller;

import com.rin.profile.dto.request.ProfileCreationRequest;
import com.rin.profile.dto.response.ApiResponse;
import com.rin.profile.dto.response.UserProfileResponse;
import com.rin.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping("/users/{profileId}")
    UserProfileResponse getUserProfile(@PathVariable String profileId) {
        return userProfileService.getUserProfile(profileId);
    }

    @GetMapping("/users")
    List<UserProfileResponse> getUserProfiles() {
        return userProfileService.getAllUserProfiles();
    }

    @GetMapping("/users/my-profile")
    ApiResponse<UserProfileResponse> getMyProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getMyProfile())
                .build();
    }

}
