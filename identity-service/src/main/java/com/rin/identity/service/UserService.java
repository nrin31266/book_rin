package com.rin.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.rin.event.dto.NotificationEvent;
import com.rin.identity.entity.Role;
import com.rin.identity.mapper.ProfileMapper;
import com.rin.identity.repository.httpclient.ProfileClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rin.identity.dto.request.UserCreationRequest;
import com.rin.identity.dto.request.UserUpdateRequest;
import com.rin.identity.dto.response.UserResponse;
import com.rin.identity.entity.User;
import com.rin.identity.exception.AppException;
import com.rin.identity.exception.ErrorCode;
import com.rin.identity.mapper.UserMapper;
import com.rin.identity.repository.RoleRepository;
import com.rin.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ProfileClient profileClient;
    ProfileMapper profileMapper;
    KafkaTemplate<String, Object> kafkaTemplate;
    public UserResponse createUser(UserCreationRequest request) {


        User user = userMapper.toUser(request);
        String userId = UUID.randomUUID().toString();
        log.info("Service: create user with id {}", userId);
        user.setId(userId);

        var profileRequest = profileMapper.toProfileCreationRequest(request);

        profileRequest.setUserId(userId);



        if(!user.getPassword().equals("email"))
            user.setPassword(passwordEncoder.encode(request.getPassword()));

        log.info("Role {}", request.getRoles());

        user.setRoles(getRoles(request.getRoles()));
        profileClient.createProfile(profileRequest);
        try {
            user = userRepository.save(user);
        }catch (DataIntegrityViolationException exception){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        //Public message to kafka
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("Well come to book rin")
                .body("Hello " + request.getUsername())
                .build();
        kafkaTemplate.send("notification-delivery", notificationEvent);
        return userMapper.toUserResponse(user);
    }
    private HashSet<Role> getRoles(List<String> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return null;
        }
        List<Role> roles = roleRepository.findAllById(requestedRoles);
        return new HashSet<>(roles);
    }

    @PreAuthorize("hasRole('ADMIN')")
    //    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponse(users);
    }

    @PostAuthorize("returnObject.id == authentication.name || hasRole('ADMIN')")
    public UserResponse getUser(String userID) {
        return userMapper.toUserResponse(
                userRepository.findById(userID).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @PostAuthorize("returnObject.id == authentication.name || hasRole('ADMIN')")
    public UserResponse updateUser(String userID, UserUpdateRequest request) {
        User user = userRepository.findById(userID).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.userUpdate(user, request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        List<Role> roles = roleRepository.findAllById(request.getRoles());

        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();

        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }
    @PostAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userID) {
        userRepository.deleteById(userID);
    }

    public void deleteUserByUserName(String userName) {
        var user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        profileClient.deleteProfile(user.getId());
        user.getRoles().clear();
        userRepository.save(user);
        userRepository.deleteById(user.getId());
    }
}
