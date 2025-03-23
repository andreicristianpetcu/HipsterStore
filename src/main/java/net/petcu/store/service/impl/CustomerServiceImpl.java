package net.petcu.store.service.impl;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import net.petcu.store.domain.Order;
import net.petcu.store.domain.OrderItem;
import net.petcu.store.domain.PricedProduct;
import net.petcu.store.domain.Product;
import net.petcu.store.domain.User;
import net.petcu.store.domain.enumeration.OrderStatus;
import net.petcu.store.exception.OrderNotFoundException;
import net.petcu.store.exception.ProductNotFoundException;
import net.petcu.store.exception.UnauthorizedException;
import net.petcu.store.exception.UserNotFoundException;
import net.petcu.store.repository.OrderRepository;
import net.petcu.store.repository.PricedProductRepository;
import net.petcu.store.repository.ProductRepository;
import net.petcu.store.repository.UserRepository;
import net.petcu.store.security.SecurityUtils;
import net.petcu.store.service.CustomerService;
import net.petcu.store.service.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PricedProductRepository pricedProductRepository;

    @Override
    public OrderDTO createOrder() {
        String currentUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new UnauthorizedException("User is not authenticated"));
        log.debug("Request to create a new order for currentUser={}", currentUser);

        User user = userRepository
            .findOneByLogin(currentUser)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUser));
        log.debug("User found in the UserId={}", user.getId());

        Order order = new Order().date(Instant.now()).status(OrderStatus.NEW).user(user);
        order = orderRepository.save(order);
        log.debug("Order created OrderId={}", order.getId());

        return new OrderDTO(order);
    }

    @Override
    public OrderDTO addItemToOrder(Long orderId, Long productId, Long quantity) {
        log.debug("Request to add item productId={} quantity={} to orderId={}", productId, quantity, orderId);

        // Find the order
        Order order = orderRepository
            .findOneWithEagerRelationships(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        // Find the product
        Product product = productRepository
            .findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        // Find the latest active priced product
        PricedProduct pricedProduct = pricedProductRepository
            .findLatestActiveByProductId(productId)
            .orElseThrow(() -> new ProductNotFoundException("No active price found for product: " + productId));

        // Create and add the order item
        OrderItem orderItem = new OrderItem().order(order).product(product).price(pricedProduct.getPrice()).quantity(quantity);

        order.addOrderItems(orderItem);

        // Update order totals
        double itemTotal = pricedProduct.getPrice().getValue() * quantity;
        order.setSubtotal(order.getSubtotal() + itemTotal);
        order.setFinalPrice(order.getSubtotal()); // Will be updated when discounts are applied

        // Save and return
        order = orderRepository.save(order);
        log.debug("Added item to order. New subtotal={}, finalPrice={}", order.getSubtotal(), order.getFinalPrice());

        return new OrderDTO(order);
    }

    @Override
    public OrderDTO applyDiscountCode(Long orderId, String discountCode) {
        log.debug("Request to apply discount code {} to order {}", discountCode, orderId);
        throw new UnsupportedOperationException("Not ready yet");
    }

    @Override
    public OrderDTO finalizeOrder(Long orderId) {
        log.debug("Request to finalize order {}", orderId);
        throw new UnsupportedOperationException("Not ready yet");
    }
}
