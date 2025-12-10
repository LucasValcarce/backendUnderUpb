package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.UserService;
import com.UnderUpb.backendUnderUpb.dto.user.UserRequestDto;
import com.UnderUpb.backendUnderUpb.dto.user.UserResponseDto;
import com.UnderUpb.backendUnderUpb.entity.User;
import com.UnderUpb.backendUnderUpb.repository.UserRepository;
import com.UnderUpb.backendUnderUpb.repository.PurchaseRepository;
import com.UnderUpb.backendUnderUpb.repository.OwnedProductRepository;
import com.UnderUpb.backendUnderUpb.dto.owned.OwnedProductDto;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchasedItemDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final OwnedProductRepository ownedProductRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userDto) {
        if (userDto == null || userDto.getName() == null) {
            throw new IllegalArgumentException("User data cannot be null");
        }
        User user = User.builder()
                .name(userDto.getName())
                .lifePoints(userDto.getLifePoints() != null ? userDto.getLifePoints() : 3)
                .score(userDto.getScore() != null ? userDto.getScore() : 0)
                .currentLevel(userDto.getCurrentLevel() != null ? userDto.getCurrentLevel() : 1)
                .build();
        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return toResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID userId, UserRequestDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getLifePoints() != null) {
            user.setLifePoints(userDto.getLifePoints());
        }
        if (userDto.getScore() != null) {
            user.setScore(userDto.getScore());
        }
        if (userDto.getCurrentLevel() != null) {
            user.setCurrentLevel(userDto.getCurrentLevel());
        }
        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", userId);
        return toResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
        log.info("User deleted with ID: {}", userId);
    }

    private UserResponseDto toResponseDto(User user) {
        List<PurchasedItemDto> purchases = purchaseRepository.findByUserId(user.getId()).stream()
            .map(p -> PurchasedItemDto.builder()
                .purchaseId(p.getId())
                .productId(p.getProduct() != null ? p.getProduct().getId() : null)
                .sku(p.getProduct() != null ? p.getProduct().getSku() : null)
                .name(p.getProduct() != null ? p.getProduct().getName() : null)
                .type(p.getProduct() != null ? p.getProduct().getType() : null)
                .quantity(p.getQuantity())
                .price(p.getPrice())
                .currency(p.getCurrency())
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .purchasedAt(p.getPurchasedAt())
                .build())
            .collect(Collectors.toList());

        List<OwnedProductDto> owned = ownedProductRepository.findByUserId(user.getId()).stream()
            .map(o -> OwnedProductDto.builder()
                .ownedProductId(o.getId())
                .productId(o.getProduct() != null ? o.getProduct().getId() : null)
                .sku(o.getProduct() != null ? o.getProduct().getSku() : null)
                .name(o.getProduct() != null ? o.getProduct().getName() : null)
                .type(o.getProduct() != null ? o.getProduct().getType() : null)
                .description(o.getProduct() != null ? o.getProduct().getDescription() : null)
                .isActive(o.getIsActive())
                .equipped(o.getEquipped())
                .purchaseId(o.getPurchase() != null ? o.getPurchase().getId() : null)
                .acquiredAt(o.getCreatedDate())
                .build())
            .collect(Collectors.toList());

        return UserResponseDto.builder()
            .id(user.getId())
            .name(user.getName())
            .lifePoints(user.getLifePoints())
            .score(user.getScore())
            .currentLevel(user.getCurrentLevel())
            .createdDate(user.getCreatedDate())
            .updatedDate(user.getUpdatedDate())
            .purchases(purchases)
            .ownedProducts(owned)
            .build();
    }
}
