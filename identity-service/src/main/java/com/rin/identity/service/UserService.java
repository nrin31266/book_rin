package com.rin.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.rin.event.dto.NotificationEvent;
import com.rin.identity.dto.request.UserCreationPasswordRequest;
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
import org.springframework.util.StringUtils;

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
        user.setId(userId);
        if(user.getPassword() != null){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        var userRole = request.getRoles().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found for ID: " + roleId)))
                .collect(Collectors.toSet());
        user.setRoles(userRole);
        try {
            if(userRepository.findByUsername(user.getUsername()).isPresent()){
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            if(userRepository.findByEmail(user.getEmail()).isPresent()){
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            user = userRepository.save(user);
        }catch (DataIntegrityViolationException exception){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(userId);
        profileClient.createProfile(profileRequest);

//        Public message to kafka
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("Well come to book rin")
                .body("Hello " + request.getUsername())
                .build();
        kafkaTemplate.send("notification-delivery", notificationEvent);
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    //    @PreAuthorize("hasAuthority('Permission?')")
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
        var userResponse = userMapper.toUserResponse(user);
        userResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));
        return userResponse;
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

    public void createUserPassword(UserCreationPasswordRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if(!StringUtils.hasText(user.getPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
        }else {
            throw new AppException(ErrorCode.PASSWORD_EXISTED);
        }

    }
}
