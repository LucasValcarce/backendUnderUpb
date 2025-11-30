package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.UserService;
import com.UnderUpb.backendUnderUpb.dto.user.UserRequestDto;
import com.UnderUpb.backendUnderUpb.dto.user.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users/players")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new player in the system")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users with pagination")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequestDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update user", description = "Partially updates a user's information")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    public ResponseEntity<UserResponseDto> patchUser(
            @PathVariable UUID id,
            @RequestBody UserRequestDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
