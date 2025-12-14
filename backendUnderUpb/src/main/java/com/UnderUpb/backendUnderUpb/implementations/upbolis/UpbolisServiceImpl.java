package com.UnderUpb.backendUnderUpb.implementations.upbolis;

import com.UnderUpb.backendUnderUpb.implementations.upbolis.upbolis.UpbolisCreatePaymentRequestDto;
import com.UnderUpb.backendUnderUpb.implementations.upbolis.upbolis.UpbolisCreatePaymentResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UpbolisServiceImpl implements UpbolisService {

    @org.springframework.beans.factory.annotation.Autowired
    public UpbolisServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${upbolis.base-url:${upbtoken.base-url:}}")
    private String baseUrl;

    @Value("${upbolis.api-key:${upbtoken.api-key:}}")
    private String apiKey;

    @Value("${upbolis.webhook-secret:${upbtoken.webhook-secret:}}")
    private String webhookSecret;

    @Value("${upbolis.callback-url:${upbtoken.callback-url:}}")
    private String callbackUrl;

    private final RestTemplate restTemplate;

    @Override
    public UpbolisCreatePaymentResponseDto createPayment(UpbolisCreatePaymentRequestDto request) throws Exception {
        String url = baseUrl + "/api/external/payments";

        JSONObject body = new JSONObject();
        if (request.getOrderId() != null) body.put("order_id", request.getOrderId().toString());
        body.put("amount", request.getAmount());
        body.put("currency", request.getCurrency());
        body.put("description", request.getDescription() != null ? request.getDescription() : "Game item");
        body.put("buyer_id", request.getBuyerId() != null ? request.getBuyerId().toString() : JSONObject.NULL);
        // webhook callback that UPBolis should call when payment status changes
        if (request.getWebhookUrl() != null) body.put("webhook_url", request.getWebhookUrl());
        else if (callbackUrl != null && !callbackUrl.isBlank()) body.put("webhook_url", callbackUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isBlank()) headers.setBearerAuth(apiKey);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Upbolis payment creation failed: " + resp.getStatusCode());
        }

        JSONObject j = new JSONObject(resp.getBody() != null ? resp.getBody() : "{}");
        String externalId = null, paymentUrl = null;
        if (j.has("externalPaymentId")) externalId = j.getString("externalPaymentId");
        if (j.has("paymentUrl")) paymentUrl = j.getString("paymentUrl");
        if (externalId == null && j.has("id")) externalId = j.getString("id");
        if (paymentUrl == null && j.has("url")) paymentUrl = j.getString("url");

        return UpbolisCreatePaymentResponseDto.builder()
                .externalPaymentId(externalId)
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    public boolean verifyWebhook(String signatureHeader, String payload) {
        try {
            String secret = webhookSecret;
            // If no webhook secret is configured, accept unsigned webhooks (useful for local testing
            // or gateways that don't sign payloads). If a secret is configured, signature header is
            // required and must match the HMAC-SHA256 of the payload.
            if (secret == null) return true;
            if (signatureHeader == null) return false;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            String computed = sb.toString();
            return computed.equalsIgnoreCase(signatureHeader);
        } catch (Exception e) {
            log.warn("Failed to verify webhook signature: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, String> parseWebhook(String payload) throws Exception {
        JSONObject j = new JSONObject(payload);
        Map<String, String> out = new HashMap<>();
        if (j.has("externalPaymentId")) out.put("externalPaymentId", j.getString("externalPaymentId"));
        if (j.has("status")) out.put("status", j.getString("status"));
        // try common variations
        if (!out.containsKey("externalPaymentId") && j.has("payment") && j.get("payment") instanceof JSONObject) {
            JSONObject p = j.getJSONObject("payment");
            if (p.has("id")) out.put("externalPaymentId", p.getString("id"));
            if (p.has("status")) out.put("status", p.getString("status"));
        }
        if (!out.containsKey("status") && j.has("state")) out.put("status", j.getString("state"));
        return out;
    }

    @Override
    public String getPaymentStatus(String externalPaymentId) throws Exception {
        String url = baseUrl + "/api/external/payments/" + externalPaymentId;
        HttpHeaders headers = new HttpHeaders();
        if (apiKey != null && !apiKey.isBlank()) headers.setBearerAuth(apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) throw new IllegalStateException("Upbolis status check failed: " + resp.getStatusCode());
        JSONObject j = new JSONObject(resp.getBody() != null ? resp.getBody() : "{}");
        if (j.has("status")) return j.getString("status");
        if (j.has("state")) return j.getString("state");
        if (j.has("payment") && j.get("payment") instanceof JSONObject) {
            JSONObject p = j.getJSONObject("payment");
            if (p.has("status")) return p.getString("status");
        }
        return null;
    }
}


