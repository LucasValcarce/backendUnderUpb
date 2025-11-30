package com.UnderUpb.backendUnderUpb.config;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import com.UnderUpb.backendUnderUpb.exception.InvalidJwtAuthenticationException;

import java.io.Serializable;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenProvider implements Serializable {
    private static final String USER_ID_CLAIM = "user_id";
    private static final String USER_ROLE_CLAIM = "user_role";

    @Value("${security.jwt.token.secret-key:Ch4ng1t}")
    private String secretKey;
    private byte[] secretKeyByte;
    @Value("${security_jwt_token_expire-length:28800000}")
    private int validityInMinutes;

    @PostConstruct
    protected void init() {
        secretKeyByte = Base64.getDecoder().decode(secretKey);
    }

    public String createToken(User user) {
        Date now = new Date();
        Date validity = plusMinutes(now, this.validityInMinutes);
        Claims claims = Jwts.claims()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(validity);
        claims.put(USER_ID_CLAIM, user.getUsername());
        claims.put(USER_ROLE_CLAIM, "USER");

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKeyByte)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getUserIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.get("user_id").toString());
    }

    public String getIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
        log.info("ID: {}", claims.getId());
        return String.valueOf(claims.getId());
    }

    public String getRoleFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
        log.info("Role: {}", claims.get(USER_ROLE_CLAIM));
        return String.valueOf(claims.get(USER_ROLE_CLAIM));
    }

    public Long getId(String token) {
        String id= Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getId();
        return Long.parseLong(id);
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            try {
                return bearerToken.substring(7);
            } catch (Exception e) {
                log.error("Error al resolver token");
                return null;
            }
        }
        return null;
    }

    public Optional<Authentication> validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            if (claims.getBody().getExpiration().after(new Date())) {
                User authUser = new User(claims.getBody().getSubject(), "", java.util.Collections.emptyList());
                return Optional.of(new UsernamePasswordAuthenticationToken(authUser, "", authUser.getAuthorities()));
            }
            return Optional.empty();
        } catch (ExpiredJwtException e) {
            log.warn("La sesión del usuario a expirado");
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error al validar el TOKEN", e);
            throw new InvalidJwtAuthenticationException("Token JWT caducado o no válido.");
        }
    }


    public  Date plusMinutes(Date date, int minutesToAdd) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(Calendar.MINUTE, minutesToAdd);

        return calDateStart.getTime();
    }



}

