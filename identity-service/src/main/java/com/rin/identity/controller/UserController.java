package com.rin.identity.controller;

import java.util.List;

import com.rin.identity.dto.request.UserCreationPasswordRequest;
import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.rin.identity.dto.request.ApiResponse;
import com.rin.identity.dto.request.UserCreationRequest;
import com.rin.identity.dto.request.UserUpdateRequest;
import com.rin.identity.dto.response.UserResponse;
import com.rin.identity.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;

    @PostMapping("/registration")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Controller: create user");
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }
    @PostMapping("/create-password")
    ApiResponse<Void> createPassword(@RequestBody @Valid UserCreationPasswordRequest request) {
        userService.createUserPassword(request);
        return ApiResponse.<Void>builder()
                .message("Password has been created, you could use it to login system")
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username:{}", authentication.getName());

        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userID}")
    UserResponse updateUser(@PathVariable("userID") String userID, @RequestBody @Valid UserUpdateRequest request) {
        return userService.updateUser(userID, request);
    }

    @DeleteMapping("/{userID}")
    String deleteUser(@PathVariable("userID") String userID) {
        userService.deleteUser(userID);
        return "Successfully delete a user!";
    }

    @DeleteMapping("/delete-by-username/{username}")
    ApiResponse deleteUserByUserName(@PathVariable("username") String userName) {
        userService.deleteUserByUserName(userName);
        return ApiResponse.builder()
                .message("Successfully delete a user with username: " + userName)
                .build();
    }
}
