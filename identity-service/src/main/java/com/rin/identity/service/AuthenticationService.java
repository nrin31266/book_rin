package com.rin.identity.service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.rin.identity.dto.request.*;
import com.rin.identity.mapper.UserMapper;
import com.rin.identity.repository.httpclient.OutboundIdentityClient;
import com.rin.identity.repository.httpclient.OutboundUserClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rin.identity.dto.response.AuthenticationResponse;
import com.rin.identity.dto.response.IntrospectResponse;
import com.rin.identity.entity.InvalidatedToken;
import com.rin.identity.entity.User;
import com.rin.identity.exception.AppException;
import com.rin.identity.exception.ErrorCode;
import com.rin.identity.repository.InvalidatedTokenRepository;
import com.rin.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;
    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;
    UserMapper userMapper;

    UserService userService;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;


    public AuthenticationResponse authenticated(AuthenticationRequest request) {
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();

        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;
        try {
            tokenIsExpired(verifyToken(token));
        } catch (JOSEException | ParseException e) {
            isValid = false;
            log.info("Invalid token");
        }
        return IntrospectResponse.builder().
                valid(isValid)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken());

        Instant expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
        Instant now = Instant.now();

        Duration timeRemaining = Duration.between(now, expiryTime);
        Duration fifteenMinutes = Duration.ofMinutes(59);

        if (timeRemaining.isNegative() || timeRemaining.compareTo(fifteenMinutes) <= 0) {
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            var newToken = generateToken(user);

            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(java.sql.Date.from(expiryTime))
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);

            return AuthenticationResponse.builder()
                    .token(newToken)
                    .build();
        } else {
            return AuthenticationResponse.builder()
                    .build();
        }
    }

    @NonFinal
    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    @Value("authorization_code")
    protected String GRANT_TYPE;

    public AuthenticationResponse outboundAuthentication(String code) {
        var response = outboundIdentityClient.exchangeToken(
                ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .redirectUri(REDIRECT_URI)
                        .grantType(GRANT_TYPE)
                        .build()
        );
        //Get user info
        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
        log.info("USER INFO: {}", userInfo);
        //Onboard user
        Optional<User> user = userRepository.findByUsername(userInfo.getEmail());
        if(user.isEmpty()){
            UserCreationRequest userCreationRequest =
                    UserCreationRequest.builder()
                            .username(userInfo.getEmail())
                            .lastName(userInfo.getFamilyName())
                            .firstName(userInfo.getGivenName())
                            .email(userInfo.getEmail())
                            .password(null)
                            .roles(List.of("USER"))
                            .build();
            var userResponse  = userService.createUser(userCreationRequest);
            user = Optional.of(userMapper.toUser(userResponse));
        }
        //Convert token
        var token = generateToken(user.get());

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("rin.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());

                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission ->  stringJoiner.add(permission.getName()));
                }
            });
        }

        return stringJoiner.toString();
    }



    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        log.info("Verifying token");
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(verifier);

        if (!verified) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private void tokenIsExpired(SignedJWT signedJWT) throws ParseException {
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiredTime.before(new Date())) {
            throw new AppException(ErrorCode.EXPIRED);
        }
    }
}
