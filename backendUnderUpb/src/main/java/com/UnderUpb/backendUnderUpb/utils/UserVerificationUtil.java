package com.UnderUpb.backendUnderUpb.utils;

import com.UnderUpb.backendUnderUpb.config.JwtTokenProvider;
import com.UnderUpb.backendUnderUpb.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserVerificationUtil {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserVerificationUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void verifyUser(HttpServletRequest request, Long userIdToCheck) {
        String token = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));
        if (token == null) {
            throw new UnauthorizedAccessException("No authorization token provided");
        }

        Long jwtUserId = jwtTokenProvider.getId(token);

        if (!jwtUserId.equals(userIdToCheck)) {
            throw new UnauthorizedAccessException(
                    String.format("User ID in token (%d) doesn't match requested user ID (%d)",
                            jwtUserId, userIdToCheck)
            );
        }
    }

    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));
        if (token == null) {
            throw new UnauthorizedAccessException("No authorization token provided");
        }
        return jwtTokenProvider.getId(token);
    }
}