package net.petcu.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private static final Long DEFAULT_QUANTITY = 2L;
    private static final Double DEFAULT_PRICE = 10.0;
    private User user;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(orderRepository, userRepository, productRepository, pricedProductRepository);
        this.user = createUser(DEFAULT_LOGIN, 1L);
    }

    @Test
    void GivenAnExistingUser_WhenCreateNewOrder_ShouldSaveOrderToDb() {
        // Arrange
        Order expectedOrder = createOrder(DEFAULT_ORDER_ID, user);
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(DEFAULT_LOGIN));
            when(userRepository.findOneByLogin(DEFAULT_LOGIN)).thenReturn(Optional.of(user));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                assertThatOrderIsEqual(invocation.getArgument(0), expectedOrder);
                return expectedOrder;
            });

            // Act
            OrderDTO result = customerService.createOrder();

            // Assert
            assertOrderDtoIsEqual(result, expectedOrder);
            verify(userRepository).findOneByLogin(DEFAULT_LOGIN);
        }
    }

    @Test
    void GivenMissingUser_WhenCreateNewOrder_ThenShouldThrowUnauthorizedException() {
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
    void GivenValidOrderAndProduct_WhenAddItemToOrder_ShouldUpdateOrderWithNewItem() {
        // Arrange
        Order order = createOrder(DEFAULT_ORDER_ID, user);
        Product product = createProduct(DEFAULT_PRODUCT_ID, "Test Product");
        PricedProduct pricedProduct = createPricedProduct(3L, product, DEFAULT_PRICE);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            setupSecurityContext(securityUtils);
            setupRepositoryMocks(order, product, pricedProduct);

            // Act
            OrderDTO result = customerService.addItemToOrder(DEFAULT_ORDER_ID, DEFAULT_PRODUCT_ID, DEFAULT_QUANTITY);

            // Assert
            assertOrderResult(result, order, product, pricedProduct);
            verifyRepositoryInteractions(order, product);
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
    }

    private void setupSecurityContext(MockedStatic<SecurityUtils> securityUtils) {
        securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of(DEFAULT_LOGIN));
    }

    private void setupRepositoryMocks(Order order, Product product, PricedProduct pricedProduct) {
        when(orderRepository.findOneWithEagerRelationships(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepository.findById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));
        when(pricedProductRepository.findLatestActiveByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(pricedProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            assertSavedOrderItem(savedOrder, product, pricedProduct);
            return savedOrder;
        });
    }

    private void assertSavedOrderItem(Order savedOrder, Product product, PricedProduct pricedProduct) {
        assertThat(savedOrder.getOrderItems()).hasSize(1);
        OrderItem savedItem = savedOrder.getOrderItems().iterator().next();
        assertOrderItemIsValid(savedItem, product, pricedProduct, DEFAULT_QUANTITY);
    }

    private void assertOrderResult(OrderDTO result, Order order, Product product, PricedProduct pricedProduct) {
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(DEFAULT_ORDER_ID);
        assertOrderTotalsAreCorrect(order, DEFAULT_PRICE * DEFAULT_QUANTITY, DEFAULT_PRICE * DEFAULT_QUANTITY);
    }

    private void verifyRepositoryInteractions(Order order, Product product) {
        verify(orderRepository).findOneWithEagerRelationships(DEFAULT_ORDER_ID);
        verify(productRepository).findById(DEFAULT_PRODUCT_ID);
        verify(pricedProductRepository).findLatestActiveByProductId(DEFAULT_PRODUCT_ID);
        verify(orderRepository).save(order);
    }

    private User createUser(String login, Long id) {
        User user = new User();
        user.setLogin(login);
        user.setId(id);
        return user;
    }

    private Order createOrder(Long id, User user) {
        return new Order().id(id).date(Instant.now()).status(OrderStatus.NEW).user(user).subtotal(0.0d).finalPrice(0.0d);
    }

    private Product createProduct(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        return product;
    }

    private PricedProduct createPricedProduct(Long id, Product product, Double priceValue) {
        PricedProduct pricedProduct = new PricedProduct();
        pricedProduct.setId(id);
        var price = new Price();
        price.setValue(priceValue);
        pricedProduct.setPrice(price);
        pricedProduct.setProduct(product);
        pricedProduct.setActive(true);
        return pricedProduct;
    }

    private static void assertThatOrderIsEqual(Order actualOrder, Order expectedOrder) {
        assertThat(actualOrder.getUser()).isEqualTo(expectedOrder.getUser());
        assertThat(actualOrder.getStatus()).isEqualTo(expectedOrder.getStatus());
        assertThat(actualOrder.getSubtotal()).isEqualTo(expectedOrder.getSubtotal());
        assertThat(actualOrder.getFinalPrice()).isEqualTo(expectedOrder.getFinalPrice());
        assertThat(actualOrder.getDate()).isCloseTo(expectedOrder.getDate(), within(1, ChronoUnit.SECONDS));
    }

    private void assertOrderDtoIsEqual(OrderDTO actual, Order expectedOrder) {
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expectedOrder.getId());
        assertThat(actual.status()).isEqualTo(expectedOrder.getStatus());
        assertThat(actual.userLogin()).isEqualTo(expectedOrder.getUser().getLogin());
        assertThat(actual.subtotal()).isEqualTo(expectedOrder.getSubtotal());
        assertThat(actual.finalPrice()).isEqualTo(expectedOrder.getFinalPrice());
    }

    private void assertOrderItemIsValid(OrderItem item, Product product, PricedProduct pricedProduct, Long quantity) {
        assertThat(item).isNotNull();
        assertThat(item.getQuantity()).isEqualTo(quantity);
        assertThat(item.getPrice().getValue()).isEqualTo(pricedProduct.getPrice().getValue());
        assertThat(item.getProduct()).isEqualTo(product);
    }

    private void assertOrderTotalsAreCorrect(Order order, Double expectedSubtotal, Double expectedFinalPrice) {
        assertThat(order.getSubtotal()).isEqualTo(expectedSubtotal);
        assertThat(order.getFinalPrice()).isEqualTo(expectedFinalPrice);
    }
}
