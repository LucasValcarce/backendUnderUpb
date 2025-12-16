package com.UnderUpb.backendUnderUpb.implementations.upost;

import com.UnderUpb.backendUnderUpb.dto.upost.UpostShareRequestDto;
import com.UnderUpb.backendUnderUpb.dto.upost.UpostShareResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpostShareServiceImpl implements UpostShareService {

    private final UpostApiClient upostApiClient;

    @Override
    public UpostShareResponseDto share(UpostShareRequestDto request) {
        // No persistence: just forward the request to upost and return its response
        log.debug("Forwarding share request to Upost for email {}", request.getEmailUsuario());
        return upostApiClient.sharePost(request);
    }
}
