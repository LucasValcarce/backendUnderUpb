package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.ProductService;
import com.UnderUpb.backendUnderUpb.dto.product.ProductRequestDto;
import com.UnderUpb.backendUnderUpb.dto.product.ProductResponseDto;
import com.UnderUpb.backendUnderUpb.entity.Product;
import com.UnderUpb.backendUnderUpb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.UnderUpb.backendUnderUpb.event.ProductChangeEvent;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto productDto) {
        var product = Product.builder()
                .name(productDto.getName())
                .type(productDto.getType())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .currency(productDto.getCurrency())
                .build();
        var saved = productRepository.save(product);
        // Publicar evento para sincronización automática (creación)
        eventPublisher.publishEvent(ProductChangeEvent.builder()
                .product(saved)
                .action(ProductChangeEvent.Action.CREATED)
                .build());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(UUID id) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        return toDto(p);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(UUID id, ProductRequestDto productDto) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        if (productDto.getName() != null) product.setName(productDto.getName());
        if (productDto.getType() != null) product.setType(productDto.getType());
        if (productDto.getDescription() != null) product.setDescription(productDto.getDescription());
        if (productDto.getPrice() != null) product.setPrice(productDto.getPrice());
        if (productDto.getCurrency() != null) product.setCurrency(productDto.getCurrency());
        var saved = productRepository.save(product);
        // Publicar evento para sincronización automática (actualización)
        eventPublisher.publishEvent(ProductChangeEvent.builder()
                .product(saved)
                .action(ProductChangeEvent.Action.UPDATED)
                .build());
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        // Eliminamos el producto localmente
        productRepository.deleteById(id);

        // Publicar evento para sincronización automática (eliminación)
        eventPublisher.publishEvent(ProductChangeEvent.builder()
                .product(product)
                .action(ProductChangeEvent.Action.DELETED)
                .build());
    }

    private ProductResponseDto toDto(Product p) {
        return ProductResponseDto.builder()
                .id(p.getId())
                .name(p.getName())
                .type(p.getType())
                .description(p.getDescription())
                .price(p.getPrice())
                .currency(p.getCurrency())
                .createdDate(p.getCreatedDate())
                .updatedDate(p.getUpdatedDate())
                .build();
    }
}
