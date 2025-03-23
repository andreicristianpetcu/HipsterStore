package net.petcu.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import net.petcu.store.domain.*;
import net.petcu.store.domain.enumeration.OrderStatus;
import net.petcu.store.exception.OrderNotFoundException;
import net.petcu.store.exception.UnauthorizedException;
import net.petcu.store.repository.OrderRepository;
import net.petcu.store.repository.PricedProductRepository;
import net.petcu.store.repository.ProductRepository;
import net.petcu.store.repository.UserRepository;
import net.petcu.store.security.SecurityUtils;
import net.petcu.store.service.dto.OrderDTO;
import net.petcu.store.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PricedProductRepository pricedProductRepository;

    private CustomerService customerService;

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final Long DEFAULT_ORDER_ID = 1L;
    private static final Long DEFAULT_PRODUCT_ID = 2L;
    private static final Integer DEFAULT_QUANTITY = 2;
    private static final Double DEFAULT_PRICE = 10.0;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(orderRepository, userRepository, productRepository, pricedProductRepository);
    }

    @Test
    void GivenAnExistingUser_WhenCreateNewOrder_ShouldSaveOrderToDb() {
        // Arrange
        User user = new User();
        user.setLogin(DEFAULT_LOGIN);
        user.setId(1L);

        Order expectedOrder = new Order().id(1L).date(Instant.now()).status(OrderStatus.NEW).user(user);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(DEFAULT_LOGIN));

            when(userRepository.findOneByLogin(DEFAULT_LOGIN)).thenReturn(Optional.of(user));
            when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);

            // Act
            OrderDTO result = customerService.createOrder();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(expectedOrder.getId());
            assertThat(result.status()).isEqualTo(OrderStatus.NEW);
            assertThat(result.userLogin()).isEqualTo(DEFAULT_LOGIN);
            assertThat(result.subtotal()).isEqualTo(0);
            assertThat(result.finalPrice()).isEqualTo(0);

            verify(userRepository).findOneByLogin(DEFAULT_LOGIN);
            verify(orderRepository).save(any(Order.class));
        }
    }

    @Test
    void GivenMissingUser_WhenCreateOrder_ThenShouldThrowUnauthorizedException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> customerService.createOrder())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User is not authenticated");

            verify(orderRepository, never()).save(any());
        }
    }

    @Test
    @Disabled
    void GivenValidOrderAndProduct_WhenAddItemToOrder_ShouldUpdateOrderWithNewItem() {
        // Arrange
        User user = new User();
        user.setLogin(DEFAULT_LOGIN);
        user.setId(1L);

        Order order = new Order().id(DEFAULT_ORDER_ID).date(Instant.now()).status(OrderStatus.NEW).user(user);

        Product product = new Product();
        product.setId(DEFAULT_PRODUCT_ID);
        product.setName("Test Product");

        PricedProduct pricedProduct = new PricedProduct();
        pricedProduct.setId(3L);
        //        pricedProduct.setPrice(DEFAULT_PRICE);
        pricedProduct.setProduct(product);
        pricedProduct.setActive(true);
        //        pricedProduct.setStartDate(Instant.now().minusSeconds(3600));
        //        pricedProduct.setEndDate(Instant.now().plusSeconds(3600));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(4L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        //        orderItem.setPrice(pricedProduct);
        //        orderItem.setQuantity(DEFAULT_QUANTITY);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(DEFAULT_LOGIN));

            when(orderRepository.findOneWithEagerRelationships(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
            when(productRepository.findById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));
            when(pricedProductRepository.findLatestActiveByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(pricedProduct));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // Act
            OrderDTO result = customerService.addItemToOrder(DEFAULT_ORDER_ID, DEFAULT_PRODUCT_ID, DEFAULT_QUANTITY);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(DEFAULT_ORDER_ID);
            assertThat(result.subtotal()).isEqualTo(DEFAULT_PRICE * DEFAULT_QUANTITY);
            assertThat(result.finalPrice()).isEqualTo(DEFAULT_PRICE * DEFAULT_QUANTITY);
            assertThat(order.getOrderItems()).hasSize(1);
            assertThat(order.getOrderItems().iterator().next().getQuantity()).isEqualTo(DEFAULT_QUANTITY);

            verify(orderRepository).findOneWithEagerRelationships(DEFAULT_ORDER_ID);
            verify(productRepository).findById(DEFAULT_PRODUCT_ID);
            verify(pricedProductRepository).findLatestActiveByProductId(DEFAULT_PRODUCT_ID);
            verify(orderRepository).save(order);
        }
    }

    @Test
    void GivenNonExistentOrder_WhenAddItemToOrder_ShouldThrowException() {
        // Arrange
        when(orderRepository.findOneWithEagerRelationships(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customerService.addItemToOrder(DEFAULT_ORDER_ID, DEFAULT_PRODUCT_ID, DEFAULT_QUANTITY))
            .isInstanceOf(OrderNotFoundException.class)
            .hasMessage("Order not found: " + DEFAULT_ORDER_ID);

        verify(orderRepository).findOneWithEagerRelationships(DEFAULT_ORDER_ID);
        verify(productRepository, never()).findById(any());
        verify(pricedProductRepository, never()).findLatestActiveByProductId(any());
        verify(orderRepository, never()).save(any());
    }
}
