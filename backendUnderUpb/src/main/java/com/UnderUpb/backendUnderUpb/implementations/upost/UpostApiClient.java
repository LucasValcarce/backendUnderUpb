package com.UnderUpb.backendUnderUpb.implementations.upost;

import com.UnderUpb.backendUnderUpb.dto.upost.UpostShareRequestDto;
import com.UnderUpb.backendUnderUpb.dto.upost.UpostShareResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpostApiClient {

    private final @Qualifier("upostRestTemplate") RestTemplate upostRestTemplate;

    @Value("${upost.base-url}")
    private String baseUrl;

    public UpostShareResponseDto sharePost(UpostShareRequestDto request) {
        String url = baseUrl + "/api/v1/posts/compartir";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<UpostShareRequestDto> entity = new HttpEntity<>(request, headers);

            upostRestTemplate.postForEntity(url, entity, String.class);
            return new UpostShareResponseDto(true);
        } catch (Exception e) {
            log.error("Unexpected error calling upost", e);
            return new UpostShareResponseDto(false);
        }
    }
}
