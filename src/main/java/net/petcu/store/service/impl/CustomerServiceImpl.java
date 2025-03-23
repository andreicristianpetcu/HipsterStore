package net.petcu.store.service.impl;

import java.time.Instant;
import net.petcu.store.domain.Order;
import net.petcu.store.domain.User;
import net.petcu.store.domain.enumeration.OrderStatus;
import net.petcu.store.repository.OrderRepository;
import net.petcu.store.repository.UserRepository;
import net.petcu.store.security.SecurityUtils;
import net.petcu.store.service.CustomerService;
import net.petcu.store.service.dto.OrderDTO;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public CustomerServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OrderDTO createOrder() {
        String currentUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user not found"));

        log.debug("Request to create a new order for user: {}", currentUser);

        User user = userRepository.findOneByLogin(currentUser).orElseThrow(() -> new IllegalStateException("User not found"));

        Order order = new Order().date(Instant.now()).status(OrderStatus.NEW).user(user);

        order = orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Override
    public OrderDTO addItemToOrder(Long orderId, Long productId, Integer quantity) {
        log.debug("Request to add item {} (quantity: {}) to order {}", productId, quantity, orderId);
        throw new NotImplementedException("Not ready yet");
    }

    @Override
    public OrderDTO applyDiscountCode(Long orderId, String discountCode) {
        log.debug("Request to apply discount code {} to order {}", discountCode, orderId);
        throw new NotImplementedException("Not ready yet");
    }

    @Override
    public OrderDTO finalizeOrder(Long orderId) {
        log.debug("Request to finalize order {}", orderId);
        throw new NotImplementedException("Not ready yet");
    }
}
