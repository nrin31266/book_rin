package com.rin.identity.configuration;

import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.rin.identity.dto.request.IntrospectRequest;
import com.rin.identity.service.AuthenticationService;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    // Annotation @Value để lấy giá trị từ file cấu hình ứng dụng
    @Value("${jwt.signerKey}")
    private String signerKey;

    // Annotation @Autowired để inject đối tượng AuthenticationService
    private final AuthenticationService authenticationService;

    public CustomJwtDecoder(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    // Biến để giữ instance của NimbusJwtDecoder
    private NimbusJwtDecoder nimbusJwtDecoder = null;



    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return new Jwt(token,
                    signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
                    signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims()
                    );
        } catch (ParseException e) {
            throw new JwtException("Invalid token");
        }

    }
}
