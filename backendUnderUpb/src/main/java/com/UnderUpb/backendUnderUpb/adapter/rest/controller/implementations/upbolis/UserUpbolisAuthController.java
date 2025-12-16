package com.UnderUpb.backendUnderUpb.adapter.rest.controller.implementations.upbolis;

import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisLoginRequestDto;
import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisLoginResponseDto;
import com.UnderUpb.backendUnderUpb.implementations.upbolis.UpbilisPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Upbolis - Users", description = "Endpoints for user authentication and status with Upbolis")
public class UserUpbolisAuthController {

    private final UpbilisPurchaseService upbilisPurchaseService;

    /**
     * Autentica un usuario con sus credenciales de Upbolis
     * POST /api/users/{userId}/authenticate-upbolis
     */
    @PostMapping("/{userId}/authenticate-upbolis")
    public ResponseEntity<?> authenticateUpbolis(
            @PathVariable UUID userId,
            @RequestBody UpbolisLoginRequestDto loginRequest) {
        try {
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body("Email and password are required");
            }

            UpbolisLoginResponseDto response = upbilisPurchaseService.authenticateUserWithUpbolis(
                    userId,
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception e) {
            log.error("Error authenticating user with Upbolis", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed to authenticate with Upbolis: " + e.getMessage());
        }
    }

    /**
     * Verifica si el usuario tiene un token válido de Upbolis
     * GET /api/users/{userId}/upbolis-status
     */
    @GetMapping("/{userId}/upbolis-status")
    public ResponseEntity<?> checkUpbolisStatus(@PathVariable UUID userId) {
        try {
            boolean isValid = upbilisPurchaseService.validateUserUpbolisToken(userId);
            
            return ResponseEntity.ok()
                    .body("{ \"authenticated\": " + isValid + " }");
        } catch (Exception e) {
            log.error("Error checking Upbolis status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found or error checking status");
        }
    }
}
