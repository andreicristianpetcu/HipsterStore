package net.petcu.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import net.petcu.store.domain.Order;
import net.petcu.store.domain.User;
import net.petcu.store.domain.enumeration.OrderStatus;
import net.petcu.store.exception.UnauthorizedException;
import net.petcu.store.exception.UserNotFoundException;
import net.petcu.store.repository.OrderRepository;
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

    private CustomerService customerService;

    private static final String DEFAULT_LOGIN = "johndoe";

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(orderRepository, userRepository);
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
}
