package com.rin.identity.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Định nghĩa các endpoint công khai (không yêu cầu xác thực)
    private static final String[] PUBLIC_ENDPOINTS = {
        "/users", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };

    private final CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {
        this.customJwtDecoder = customJwtDecoder;
    }

    // Bean cấu hình chuỗi bộ lọc bảo mật
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        // Cấu hình các yêu cầu HTTP
        httpSecurity.authorizeHttpRequests(
                request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                        .permitAll() // Cho phép các POST request đến các endpoint công khai
                        .anyRequest()
                        .authenticated() // Yêu cầu xác thực cho các yêu cầu khác
                );

        // Cấu hình OAuth2 Resource Server
        httpSecurity.oauth2ResourceServer(
                osutj2 -> osutj2.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder) // Sử dụng CustomJwtDecoder để giải mã JWT
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter())) // Sử dụng jwtAuthenticationConverter tùy chỉnh
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // Xử lý lỗi xác thực bằng JwtAuthenticationEntryPoint
                );

        // Vô hiệu hóa CSRF (Cross-Site Request Forgery)
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    // Bean tạo JwtAuthenticationConverter tùy chỉnh
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // Không đặt tiền tố cho các quyền

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration corsConfiguration= new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("http://localhost:8888");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource= new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);


        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    // Bean tạo PasswordEncoder sử dụng BCrypt với độ mạnh là 10
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
