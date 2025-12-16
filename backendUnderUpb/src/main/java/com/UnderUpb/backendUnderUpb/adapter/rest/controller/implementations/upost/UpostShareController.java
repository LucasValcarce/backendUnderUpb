package com.UnderUpb.backendUnderUpb.adapter.rest.controller.implementations.upost;

import com.UnderUpb.backendUnderUpb.dto.upost.UpostShareRequestDto;
import com.UnderUpb.backendUnderUpb.implementations.upost.UpostShareService;
import com.UnderUpb.backendUnderUpb.dto.upost.UpostShareResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Upost - Posts", description = "Endpoints for sharing posts to Upost")
public class UpostShareController {

    private final UpostShareService upostShareService;

    @PostMapping("/compartir")
    public ResponseEntity<?> compartir(@RequestBody UpostShareRequestDto request) {
        try {
            if (request.getEmailUsuario() == null || request.getPassword() == null || request.getMensaje() == null) {
                return ResponseEntity.badRequest().body("emailUsuario, password and mensaje are required");
            }

            UpostShareResponseDto response = upostShareService.share(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
            }
        } catch (Exception e) {
            log.error("Error sharing to Upost", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sharing to Upost: " + e.getMessage());
        }
    }
}
