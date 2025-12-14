package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.product.ProductRequestDto;
import com.UnderUpb.backendUnderUpb.dto.product.ProductResponseDto;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto productDto);
    ProductResponseDto getProductById(UUID id);
    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    ProductResponseDto updateProduct(UUID id, ProductRequestDto productDto);
    void deleteProduct(UUID id);
}
