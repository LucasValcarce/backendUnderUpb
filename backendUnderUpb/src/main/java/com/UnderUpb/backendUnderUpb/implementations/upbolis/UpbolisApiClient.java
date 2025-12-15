package com.UnderUpb.backendUnderUpb.implementations.upbolis;

import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisLoginRequestDto;
import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisLoginResponseDto;
import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisProductDto;
import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisCreateProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbolisApiClient {

    private final RestTemplate restTemplate;

    @Value("${upbolis.api.url}")
    private String upbolisApiUrl;

    private String buildUrl(String path) {
        String base = upbolisApiUrl == null ? "" : upbolisApiUrl;
        if (!base.endsWith("/")) base = base + "/";
        if (path.startsWith("/")) path = path.substring(1);
        return base + path;
    }

    @Value("${upbolis.system.email}")
    private String systemEmail;

    @Value("${upbolis.system.password}")
    private String systemPassword;

    @Value("${upbolis.system.token:}")
    private String systemTokenOverride;

    /**
     * Autentica un usuario en Upbolis
     */
    public UpbolisLoginResponseDto loginUser(String email, String password) {
        try {
            String url = buildUrl("auth/login");
            log.debug("Upbolis login URL: {}", url);
            
            UpbolisLoginRequestDto loginRequest = UpbolisLoginRequestDto.builder()
                    .email(email)
                    .password(password)
                    .build();

            // Log the outgoing payload for debugging (do not log in production with real passwords)
            try {
                ObjectMapper om = new ObjectMapper();
                log.debug("Upbolis login payload: {}", om.writeValueAsString(loginRequest));
            } catch (Exception ignored) {
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<UpbolisLoginRequestDto> request = new HttpEntity<>(loginRequest, headers);

            ResponseEntity<UpbolisLoginResponseDto> response = restTemplate.postForEntity(
                    url,
                    request,
                    UpbolisLoginResponseDto.class
            );

                log.debug("Upbolis login response: status={} headers={}", response.getStatusCode(), response.getHeaders());
            log.info("User login successful for email: {}", email);
            return response.getBody();

        } catch (HttpClientErrorException.Unauthorized ue) {
            String body = ue.getResponseBodyAsString();
            log.error("Unauthorized when logging in user: status={} headers={} body={}", ue.getStatusCode(), ue.getResponseHeaders(), body);
            throw new RuntimeException("Failed to authenticate with Upbolis: Unauthorized");
        } catch (HttpClientErrorException hcee) {
            String body = hcee.getResponseBodyAsString();
            log.error("Error logging in user: status={} headers={} body={}", hcee.getStatusCode(), hcee.getResponseHeaders(), body);
            throw new RuntimeException("Failed to authenticate with Upbolis: " + hcee.getMessage());
        } catch (RestClientException e) {
            log.error("Error logging in user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to authenticate with Upbolis: " + e.getMessage(), e);
        }
    }

    /**
     * Autentica el sistema con las credenciales del sistema
     */
    public String getSystemToken() {
        try {
            if (systemTokenOverride != null && !systemTokenOverride.isBlank()) {
                log.debug("Using configured system token (from properties)");
                return systemTokenOverride;
            }

            UpbolisLoginResponseDto response = loginUser(systemEmail, systemPassword);
            if (response != null && response.getToken() != null) {
                log.info("System token obtained successfully");
                return response.getToken();
            }
            throw new RuntimeException("Failed to obtain system token from Upbolis");
        } catch (Exception e) {
            log.error("Error obtaining system token", e);
            throw new RuntimeException("Failed to get system token: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un producto en Upbolis
     */
    public UpbolisProductDto createProduct(String token, String productName, Double price, String description) {
        try {
            String url = buildUrl("seller/products");
            log.debug("Upbolis create product URL: {}", url);

            UpbolisCreateProductDto productDto = UpbolisCreateProductDto.builder()
                    .name(productName)
                    .price(price)
                    .description(description)
                    .stock(99999999) // default stock when creating from local product
                    .isActive(true)
                    .build();

            log.debug("Creating Upbolis product request: name={}, price={}, stock={}", productDto.getName(), productDto.getPrice(), productDto.getStock());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<UpbolisCreateProductDto> request = new HttpEntity<>(productDto, headers);

            // Use String response so we can robustly parse id whether it's `product_id`, `id` or nested
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class
            );

            String body = response.getBody();
            log.debug("Upbolis create product response body: {}", body);

            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(body == null ? "" : body);
                JsonNode maybe = root;

                // Handle common wrappers
                if (root.has("data")) maybe = root.get("data");
                if (maybe.has("product")) maybe = maybe.get("product");

                Long id = null;
                if (maybe.has("product_id")) id = maybe.get("product_id").asLong();
                else if (maybe.has("id")) id = maybe.get("id").asLong();

                UpbolisProductDto dto = null;
                try {
                    dto = mapper.treeToValue(maybe, UpbolisProductDto.class);
                } catch (Exception ex) {
                    log.debug("Could not map response body to UpbolisProductDto: {}", ex.getMessage());
                }

                if (dto == null) dto = new UpbolisProductDto();
                if (dto.getProductId() == null && id != null) dto.setProductId(id);

                // If still null, try to extract ID from Location header
                if (dto.getProductId() == null) {
                    try {
                        URI loc = response.getHeaders().getLocation();
                        if (loc != null) {
                            String path = loc.getPath();
                            if (path != null) {
                                String[] parts = path.split("/");
                                String last = parts[parts.length - 1];
                                try {
                                    Long parsed = Long.parseLong(last);
                                    dto.setProductId(parsed);
                                } catch (NumberFormatException nfe) {
                                    log.debug("Could not parse product id from Location header: {}", last);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.debug("No Location header present or could not parse it: {}", ex.getMessage());
                    }
                }

                log.info("Product created successfully in Upbolis: {}", productName);
                return dto;
            } catch (IOException e) {
                log.error("Error parsing Upbolis create product response: {}", e.getMessage());
                throw new RuntimeException("Failed to create product in Upbolis: could not parse response: " + e.getMessage());
            }
        } catch (HttpClientErrorException hcee) {
            String body = hcee.getResponseBodyAsString();
            log.error("Error creating product in Upbolis: status={}, body={}", hcee.getStatusCode(), body);
            throw new RuntimeException("Failed to create product in Upbolis: " + hcee.getMessage());
        } catch (RestClientException e) {
            log.error("Error creating product in Upbolis", e);
            throw new RuntimeException("Failed to create product in Upbolis: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un producto en Upbolis (PUT /seller/products/{id})
     */
    public UpbolisProductDto updateProduct(String token, Long productId, UpbolisCreateProductDto updateDto) {
        try {
            String url = buildUrl("seller/products/" + productId);
            log.debug("Upbolis update product URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<UpbolisCreateProductDto> request = new HttpEntity<>(updateDto, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    String.class
            );

            String body = response.getBody();
            log.debug("Upbolis update product response body: {}", body);

            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(body == null ? "" : body);
                JsonNode maybe = root;

                if (root.has("data")) maybe = root.get("data");
                if (maybe.has("product")) maybe = maybe.get("product");

                Long id = null;
                if (maybe.has("product_id")) id = maybe.get("product_id").asLong();
                else if (maybe.has("id")) id = maybe.get("id").asLong();

                UpbolisProductDto dto = null;
                try {
                    dto = mapper.treeToValue(maybe, UpbolisProductDto.class);
                } catch (Exception ex) {
                    log.debug("Could not map response body to UpbolisProductDto: {}", ex.getMessage());
                }

                if (dto == null) dto = new UpbolisProductDto();
                if (dto.getProductId() == null && id != null) dto.setProductId(id);

                log.info("Product updated successfully in Upbolis: {}", productId);
                return dto;
            } catch (IOException e) {
                log.error("Error parsing Upbolis update product response: {}", e.getMessage());
                throw new RuntimeException("Failed to update product in Upbolis: could not parse response: " + e.getMessage());
            }
        } catch (RestClientException e) {
            log.error("Error updating product in Upbolis: {}", e.getMessage());
            throw new RuntimeException("Failed to update product in Upbolis: " + e.getMessage());
        }
    }

    /**
     * Obtiene los productos del usuario
     */
    public UpbolisProductDto[] getUserProducts(String token) {
        try {
            String url = buildUrl("seller/products");
            log.debug("Upbolis user products URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<UpbolisProductDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    UpbolisProductDto[].class
            );

            log.info("Products retrieved successfully for user");
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error retrieving user products from Upbolis: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve user products: " + e.getMessage());
        }
    }

    /**
     * Obtiene los detalles de un producto
     */
    public UpbolisProductDto getProductDetails(String token, Long productId) {
        try {
            String url = buildUrl("seller/products/" + productId);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<UpbolisProductDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    UpbolisProductDto.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error retrieving product details from Upbolis: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve product details: " + e.getMessage());
        }
    }

    /**
     * Crea una orden (compra) en Upbolis usando el token del comprador
     * POST /api/orders
     */
    public Long createOrder(String token, Long upbolisProductId, Integer quantity) {
        try {
            String url = buildUrl("orders");
            log.debug("Upbolis create order URL: {}", url);

            ObjectMapper mapper = new ObjectMapper();
            // Build payload { items: [{ product_id: ..., quantity: ... }] }
            String body = mapper.writeValueAsString(Map.of(
                    "items", List.of(Map.of(
                            "product_id", upbolisProductId,
                            "quantity", quantity != null ? quantity : 1
                    ))
            ));

            log.debug("Upbolis create order payload: {}", body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            String respBody = response.getBody();
            log.debug("Upbolis create order response: status={} headers={} body={}", response.getStatusCode(), response.getHeaders(), respBody);

            try {
                JsonNode root = mapper.readTree(respBody == null ? "" : respBody);
                JsonNode maybe = root;
                if (root.has("data")) maybe = root.get("data");
                if (maybe.has("order")) maybe = maybe.get("order");

                Long id = null;
                if (maybe.has("id")) id = maybe.get("id").asLong();
                else if (maybe.has("order_id")) id = maybe.get("order_id").asLong();

                if (id != null) {
                    log.info("Order created in Upbolis with id: {}", id);
                    return id;
                }

                // fallback: try Location header
                URI loc = response.getHeaders().getLocation();
                if (loc != null) {
                    String[] parts = loc.getPath().split("/");
                    String last = parts[parts.length - 1];
                    try { return Long.parseLong(last); } catch (NumberFormatException ignore) {}
                }

                throw new RuntimeException("Failed to extract order id from Upbolis response");
            } catch (IOException ioe) {
                log.error("Error parsing create order response: {}", ioe.getMessage());
                throw new RuntimeException("Failed to create order in Upbolis: " + ioe.getMessage(), ioe);
            }

        } catch (RestClientException e) {
            log.error("Error creating order in Upbolis: {}", e.getMessage());
            throw new RuntimeException("Failed to create order in Upbolis: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a public URL to view an order on Upbolis (for paymentUrl fields)
     */
    public String getOrderUrl(Long orderId) {
        return buildUrl("orders/" + orderId);
    }

    /**
     * Register a user in Upbolis (POST /auth/register)
     */
    public UpbolisLoginResponseDto registerUser(String name, String email, String password) {
        try {
            String url = buildUrl("auth/register");
            log.debug("Upbolis register URL: {}", url);

            com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisRegisterRequestDto dto =
                    com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisRegisterRequestDto.builder()
                            .name(name)
                            .email(email)
                            .password(password)
                            .password_confirmation(password)
                            .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisRegisterRequestDto> request = new HttpEntity<>(dto, headers);

            ResponseEntity<UpbolisLoginResponseDto> response = restTemplate.postForEntity(
                    url,
                    request,
                    UpbolisLoginResponseDto.class
            );

            log.info("User registered successfully in Upbolis: {}", email);
            return response.getBody();
        } catch (HttpClientErrorException hcee) {
            String body = hcee.getResponseBodyAsString();
            log.error("Error registering user in Upbolis: status={}, body={}", hcee.getStatusCode(), body);
            throw new RuntimeException("Failed to register user in Upbolis: " + hcee.getMessage());
        } catch (RestClientException e) {
            log.error("Error registering user in Upbolis", e);
            throw new RuntimeException("Failed to register user in Upbolis: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un producto en Upbolis (si el endpoint existe)
     */
    public void deleteProduct(String token, Long productId) {
        try {
            // Correct endpoint for seller product deletion
            String url = buildUrl("seller/products/" + productId);
            log.debug("Upbolis delete product URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<?> request = new HttpEntity<>(headers);

            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );

            log.info("Product {} deleted in Upbolis (seller/products endpoint)", productId);
        } catch (RestClientException e) {
            log.warn("Error deleting product in Upbolis (may not exist or endpoint unsupported): {}", e.getMessage());
            // No lanzar excepción para no bloquear la operación local
        }
    }
}
