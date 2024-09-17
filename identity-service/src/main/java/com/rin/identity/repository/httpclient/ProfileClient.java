    package com.rin.identity.repository.httpclient;

    import com.rin.identity.configuration.AuthenticationRequestInterceptor;
    import com.rin.identity.dto.request.ProfileCreationRequest;
    import com.rin.identity.dto.response.UserProfileResponse;
    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.http.MediaType;
    import org.springframework.web.bind.annotation.*;


    @FeignClient(name = "profile-service", url = "${app.services.profile}",
            configuration = {AuthenticationRequestInterceptor.class})
    public interface ProfileClient {
        @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
        UserProfileResponse createProfile(
                @RequestBody ProfileCreationRequest request);

        @DeleteMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
        UserProfileResponse deleteProfile(
                @PathVariable("userId") String request);
    }
