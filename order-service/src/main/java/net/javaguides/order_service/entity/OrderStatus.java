package net.javaguides.order_service.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    SHIPPING("Shipping"),
    DELIVERED("Delivered");

    public final String label;
}
