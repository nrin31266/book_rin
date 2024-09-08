package com.rin.post.repository.httpclient;

import com.rin.post.dto.ApiResponse;
import com.rin.post.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "profile-service",
        url = "${app.service.profile.url}"
)
public interface ProfileClient {
    @GetMapping("/internal/users/{userId}")
    ApiResponse<UserProfileResponse> getProfile(
        @PathVariable("userId") String userId
    );

}
