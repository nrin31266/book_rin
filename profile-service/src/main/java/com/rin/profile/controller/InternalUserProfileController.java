package com.rin.profile.controller;

import com.rin.profile.dto.request.ProfileCreationRequest;
import com.rin.profile.dto.response.ApiResponse;
import com.rin.profile.dto.response.UserProfileResponse;
import com.rin.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    UserProfileResponse createUserProfile(@RequestBody ProfileCreationRequest request) {
        return userProfileService.createProfile(request);
    }

    @GetMapping("internal/users/{userId}")
    ApiResponse<UserProfileResponse> getUserProfile(@PathVariable("userId") String userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfileById(userId))
                .build();
    }
}
