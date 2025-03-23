package net.petcu.store.service.impl;

import net.petcu.store.security.SecurityUtils;
import net.petcu.store.service.CustomerService;
import net.petcu.store.service.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Override
    public OrderDTO createOrder() {
        String currentUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user not found"));

        log.debug("Request to create a new order for user: {}", currentUser);
        // TODO: Implement actual order creation
        return new OrderDTO();
    }

    @Override
    public OrderDTO addItemToOrder(Long orderId, Long productId, Integer quantity) {
        log.debug("Request to add item {} (quantity: {}) to order {}", productId, quantity, orderId);
        // TODO: Implement actual item addition
        return new OrderDTO();
    }

    @Override
    public OrderDTO applyDiscountCode(Long orderId, String discountCode) {
        log.debug("Request to apply discount code {} to order {}", discountCode, orderId);
        // TODO: Implement actual discount application
        return new OrderDTO();
    }

    @Override
    public OrderDTO finalizeOrder(Long orderId) {
        log.debug("Request to finalize order {}", orderId);
        // TODO: Implement actual order finalization
        return new OrderDTO();
    }
}
