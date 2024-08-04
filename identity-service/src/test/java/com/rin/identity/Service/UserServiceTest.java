package com.rin.identity.Service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.rin.identity.dto.request.UserCreationRequest;
import com.rin.identity.dto.response.UserResponse;
import com.rin.identity.entity.User;
import com.rin.identity.exception.AppException;
import com.rin.identity.repository.UserRepository;
import com.rin.identity.service.UserService;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest request;

    private UserResponse userResponse;

    private LocalDate dob;

    private User user;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1900, 1, 1);

        request = UserCreationRequest.builder()
                .username("rin001")
                .password("abcd1234")
                .firstName("User")
                .lastName("User")
                .dob(dob)
                //                .roles(List.of("USER"))
                .build();

        userResponse = UserResponse.builder()
                .id("c2ec3b95-c29e-4fa2-b224-2116fdf39fc9")
                .username("rin001")
                .firstName("User")
                .lastName("User")
                .dob(dob)
                .build();

        user = User.builder()
                .id("c2ec3b95-c29e-4fa2-b224-2116fdf39fc9")
                .username("rin001")
                .firstName("User")
                .lastName("User")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.createUser(request);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("c2ec3b95-c29e-4fa2-b224-2116fdf39fc9");
        Assertions.assertThat(response.getUsername()).isEqualTo("rin001");
    }

    @Test
    void createUser_validExisted_fail() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));
        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    }

    @Test
    @WithMockUser(username = "rin001")
    void getMyInfo_validRequest_success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        var response = userService.getMyInfo();

        Assertions.assertThat(response.getUsername()).isEqualTo("rin001");
        Assertions.assertThat(response.getId()).isEqualTo("c2ec3b95-c29e-4fa2-b224-2116fdf39fc9");
    }

    @Test
    @WithMockUser(username = "rin002")
    void getMyInfo_userNotFound_error() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1006);
        Assertions.assertThat(exception.getErrorCode().getMessage()).isEqualTo("User not existed");
    }
}
