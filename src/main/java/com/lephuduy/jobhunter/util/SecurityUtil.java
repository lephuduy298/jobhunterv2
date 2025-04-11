package com.lephuduy.jobhunter.util;

import com.lephuduy.jobhunter.domain.dto.ResLoginDTO;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SecurityUtil {
    @Value("${lephuduy.jwt.base64-secret}")
    private String jwtKey;

    @Value("${lephuduy.jwt.access-token-validity-in-seconds}")
    private long jwtAccessExperation;

    @Value("${lephuduy.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshExperation;

    private final JwtEncoder jwtEncoder;

    public SecurityUtil (JwtEncoder jwtEncoder){
        this.jwtEncoder = jwtEncoder;
    }

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    public String createAccessToken(String email, ResLoginDTO loginDTO){
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtAccessExperation, ChronoUnit.SECONDS);

        ResLoginDTO.UserInsideToken userInsideToken = new ResLoginDTO.UserInsideToken(
                loginDTO.getUser().getId(),
            loginDTO.getUser().getName(),
            loginDTO.getUser().getEmail()

        );



        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("lephuduy", loginDTO.getUser())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    public String createRefreshToken(String email, ResLoginDTO loginDTO){
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtRefreshExperation, ChronoUnit.SECONDS);

        ResLoginDTO.UserInsideToken userInsideToken = new ResLoginDTO.UserInsideToken(
                loginDTO.getUser().getId(),
                loginDTO.getUser().getName(),
                loginDTO.getUser().getEmail()

        );

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("lephuduy", loginDTO.getUser())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    public Jwt checkValidToken(String refreshToken) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(JWT_ALGORITHM).build();

        try {
            return jwtDecoder.decode(refreshToken);
        } catch (Exception e) {
            System.out.println(">>> JWT error: " + e.getMessage());
            throw e;
        }
    }
}
