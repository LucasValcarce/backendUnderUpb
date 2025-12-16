package com.UnderUpb.backendUnderUpb.implementations.upost;

import com.UnderUpb.backendUnderUpb.dto.upost.UpostShareRequestDto;
import com.UnderUpb.backendUnderUpb.dto.upost.UpostShareResponseDto;

public interface UpostShareService {
    UpostShareResponseDto share(UpostShareRequestDto request);
}
