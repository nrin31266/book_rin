package com.rin.identity.configuration;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rin.identity.dto.request.ApiResponse;
import com.rin.identity.exception.ErrorCode;

// Lớp này triển khai giao diện AuthenticationEntryPoint để xử lý các yêu cầu không xác thực
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Lấy mã lỗi cho trạng thái không xác thực
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        // Đặt mã trạng thái HTTP của phản hồi
        response.setStatus(errorCode.getStatusCode().value());

        // Đặt kiểu nội dung của phản hồi là JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Tạo đối tượng ApiResponse với mã lỗi và thông điệp lỗi
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // Chuyển đổi đối tượng ApiResponse sang JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Ghi chuỗi JSON vào phản hồi HTTP
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        // Đẩy nội dung phản hồi đến client
        response.flushBuffer();
    }
}
