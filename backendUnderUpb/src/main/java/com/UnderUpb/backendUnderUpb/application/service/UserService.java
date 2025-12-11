package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.user.UserRequestDto;
import com.UnderUpb.backendUnderUpb.dto.user.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userDto);
    UserResponseDto getUserById(UUID userId);
    UserResponseDto getUserByStudentCode(Integer studentCode);
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto updateUser(UUID userId, UserRequestDto userDto);
    void deleteUser(UUID userId);
}
