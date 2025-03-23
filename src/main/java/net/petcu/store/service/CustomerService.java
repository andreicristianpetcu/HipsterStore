package net.petcu.store.service;

import net.petcu.store.service.dto.OrderDTO;

public interface CustomerService {
    OrderDTO createOrder();

    OrderDTO addItemToOrder(Long orderId, Long productId, Integer quantity);

    OrderDTO applyDiscountCode(Long orderId, String discountCode);

    OrderDTO finalizeOrder(Long orderId);
}
