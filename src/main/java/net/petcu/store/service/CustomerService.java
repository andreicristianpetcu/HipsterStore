package net.petcu.store.service;

import java.util.List;
import java.util.UUID;
import net.petcu.store.domain.Product;
import net.petcu.store.service.dto.OrderDTO;

public interface CustomerService {
    OrderDTO createOrder();

    OrderDTO addItemToOrder(Long orderId, Long productId, Long quantity);

    OrderDTO applyDiscountCode(Long orderId, UUID discountCode);

    OrderDTO finalizeOrder(Long orderId);

    List<Product> findProductsByName(String name);
}
