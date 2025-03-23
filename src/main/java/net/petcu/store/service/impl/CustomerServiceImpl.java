package net.petcu.store.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.petcu.store.domain.*;
import net.petcu.store.domain.enumeration.DiscountType;
import net.petcu.store.domain.enumeration.OrderStatus;
import net.petcu.store.exception.*;
import net.petcu.store.repository.*;
import net.petcu.store.security.SecurityUtils;
import net.petcu.store.service.CustomerService;
import net.petcu.store.service.PaymentService;
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
    private final DiscountRepository discountRepository;
    private final PaymentService paymentService;

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

        log.debug("Looking up order orderId={}", orderId);
        Order order = orderRepository
            .findOneWithEagerRelationships(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        log.debug("Looking up product productId={}", productId);
        Product product = productRepository
            .findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        log.debug("Looking up latest active price for product productId={}", productId);
        PricedProduct pricedProduct = pricedProductRepository
            .findLatestActiveByProductId(productId)
            .orElseThrow(() -> new ProductNotFoundException("No active price found for product: " + productId));

        log.debug("Creating order item with productId={} quantity={}", productId, quantity);
        OrderItem orderItem = new OrderItem().order(order).product(product).price(pricedProduct.getPrice()).quantity(quantity);

        order.addOrderItems(orderItem);

        double itemTotal = pricedProduct.getPrice().getValue() * quantity;
        log.debug("Updating order totals - adding itemTotal={} to current subtotal={}", itemTotal, order.getSubtotal());
        order.setSubtotal(order.getSubtotal() + itemTotal);
        order.setFinalPrice(order.getSubtotal()); // Will be updated when discounts are applied

        order = orderRepository.save(order);
        log.debug("Added item to order. New subtotal={}, finalPrice={}", order.getSubtotal(), order.getFinalPrice());

        return new OrderDTO(order);
    }

    @Override
    public OrderDTO applyDiscountCode(Long orderId, UUID discountCode) {
        log.debug("Request to apply discount code {} to order {}", discountCode, orderId);

        Order order = orderRepository
            .findOneWithEagerRelationships(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        log.debug("Found order orderId={} with subtotal={}", orderId, order.getSubtotal());

        if (order.getStatus() != OrderStatus.NEW) {
            log.warn("Cannot apply discount to order orderId={} with status={}", orderId, order.getStatus());
            throw new InvalidOrderStatusException("Order is not in NEW status");
        }

        List<Discount> foundDiscounts = discountRepository.findByDiscountCode(discountCode);
        if (foundDiscounts.isEmpty()) {
            throw new DiscountCodeNotFoundException("Did not find discount code=" + discountCode);
        }
        var discount = foundDiscounts.get(0);
        DiscountType discountType = discount.getDiscountType();
        double discountAmount = discount.getAmount();
        double finalPrice = calculateFinalPrice(order.getSubtotal(), discountType, discountAmount);

        log.debug("Applying discount type={} value={} to order orderId={}", discountType, discountAmount, orderId);
        order.setFinalPrice(finalPrice);
        order = orderRepository.save(order);
        log.debug("Updated order orderId={} with finalPrice={}", orderId, finalPrice);

        return new OrderDTO(order);
    }

    private double calculateFinalPrice(double subtotal, DiscountType discountType, double discountValue) {
        return switch (discountType) {
            case PERCENTAGE -> {
                if (discountValue < 0 || discountValue > 100) {
                    throw new IllegalArgumentException("Percentage discount must be between 0 and 100");
                }
                yield subtotal * (1 - discountValue / 100);
            }
            case FIXED -> {
                if (discountValue >= subtotal) {
                    throw new IllegalArgumentException("Fixed discount cannot be greater than or equal to subtotal");
                }
                yield subtotal - discountValue;
            }
        };
    }

    @Override
    public OrderDTO finalizeOrder(Long orderId) {
        log.debug("Request to finalize order {}", orderId);

        Order order = orderRepository
            .findOneWithEagerRelationships(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        log.debug("Found order orderId={} with status={}", orderId, order.getStatus());

        if (order.getStatus() != OrderStatus.NEW) {
            log.warn("Cannot finalize order orderId={} with status={}", orderId, order.getStatus());
            throw new InvalidOrderStatusException("Order is not in NEW status");
        }

        log.debug("Processing payment for order orderId={} with amount={}", orderId, order.getFinalPrice());
        boolean paymentSuccess = paymentService.processPayment(order);

        if (!paymentSuccess) {
            log.warn("Payment failed for order orderId={} due to insufficient funds", orderId);
            throw new PaymentFailedException("Payment failed: insufficient funds");
        }

        order.setStatus(OrderStatus.PAID);
        order = orderRepository.save(order);
        log.debug("Order orderId={} finalized successfully", orderId);

        return new OrderDTO(order);
    }

    @Override
    public List<Product> findProductsByName(String name) {
        log.debug("Request to find products with name containing={}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
