package com.UnderUpb.backendUnderUpb.event;

import com.UnderUpb.backendUnderUpb.entity.Product;
import lombok.Builder;
import lombok.Value;

/**
 * Evento que representa cambios en productos (creación, actualización, eliminación)
 */
@Value
@Builder
public class ProductChangeEvent {
    Product product;
    Action action;

    public enum Action {
        CREATED,
        UPDATED,
        DELETED
    }
}
