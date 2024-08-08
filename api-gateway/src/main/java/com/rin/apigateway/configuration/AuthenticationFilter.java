package com.rin.apigateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rin.apigateway.dto.ApiResponse;
import com.rin.apigateway.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.List;


@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Order {
    IdentityService identityService;
    ObjectMapper objectMapper;

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Authentication Filter...");
        // Get token from authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse());
        String token = authHeader.getFirst().replace("Bearer", "");
        log.info("Authentication Token: {}", token);
        // Verify token
        identityService.introspect(token).subscribe(introspectResponseApiResponse -> {
            log.info("Introspect Response: {}", introspectResponseApiResponse.getResult().isValid());
        });
        // Delegate identity service
        return  identityService.introspect(token).flatMap(introspectResponseApiResponse -> {
           if (introspectResponseApiResponse.getResult().isValid()) {
               return chain.filter(exchange);
           }else{
               return unauthenticated(exchange.getResponse());
           }
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }
    private Mono<Void> unauthenticated(ServerHttpResponse response){
        ApiResponse<?> apiResponse =  ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int value() {
        return -1;
    }
}