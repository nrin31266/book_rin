package com.rin.identity.configuration;

import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

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



    // Ghi đè phương thức decode từ giao diện JwtDecoder
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // Tạo một yêu cầu introspect để xác thực token
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build());
            if (!response.isValid()) {
                throw new JwtException("Token invalid");
            }
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }

        // Nếu nimbusJwtDecoder chưa được khởi tạo, thì khởi tạo nó
        if (Objects.isNull(nimbusJwtDecoder)) {
            // Tạo khóa bí mật từ signerKey với thuật toán HS512
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        // Giải mã và trả về đối tượng Jwt
        return nimbusJwtDecoder.decode(token);
    }
}
