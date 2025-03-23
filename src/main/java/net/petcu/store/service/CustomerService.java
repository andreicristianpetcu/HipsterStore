package net.petcu.store.service;

import java.util.List;
import net.petcu.store.domain.Product;
import net.petcu.store.service.dto.OrderDTO;

public interface CustomerService {
    OrderDTO createOrder();

    OrderDTO addItemToOrder(Long orderId, Long productId, Long quantity);

    OrderDTO applyDiscountCode(Long orderId, String discountCode);

    OrderDTO finalizeOrder(Long orderId);

    List<Product> findProductsByName(String name);
}
