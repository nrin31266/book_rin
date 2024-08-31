package com.rin.identity.repository.httpclient;

import com.rin.identity.dto.request.ExchangeTokenRequest;
import com.rin.identity.dto.response.ExchangeTokenResponse;
import com.rin.identity.dto.response.OutboundUserResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "outbound-user-client",
        url = "https://www.googleapis.com"
)
public interface OutboundUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUserResponse getUserInfo(
            @RequestParam("alt") String alt,
            @RequestParam("access_token") String token
    );
}
