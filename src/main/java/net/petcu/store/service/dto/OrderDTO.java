package net.petcu.store.service.dto;

import java.time.Instant;
import net.petcu.store.domain.Order;
import net.petcu.store.domain.enumeration.OrderStatus;

public record OrderDTO(Long id, Instant date, OrderStatus status, String userLogin, Double subtotal, Double finalPrice) {
    public OrderDTO(Order order) {
        this(order.getId(), order.getDate(), order.getStatus(), order.getUser().getLogin(), order.getSubtotal(), order.getFinalPrice());
    }
}
