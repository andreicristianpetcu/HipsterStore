package net.petcu.store.domain;

import static net.petcu.store.domain.DiscountTestSamples.*;
import static net.petcu.store.domain.OrderItemTestSamples.*;
import static net.petcu.store.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import net.petcu.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Order.class);
        Order order1 = getOrderSample1();
        Order order2 = new Order();
        assertThat(order1).isNotEqualTo(order2);

        order2.setId(order1.getId());
        assertThat(order1).isEqualTo(order2);

        order2 = getOrderSample2();
        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    void orderItemsTest() {
        Order order = getOrderRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        order.addOrderItems(orderItemBack);
        assertThat(order.getOrderItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(order);

        order.removeOrderItems(orderItemBack);
        assertThat(order.getOrderItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();

        order.orderItems(new HashSet<>(Set.of(orderItemBack)));
        assertThat(order.getOrderItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(order);

        order.setOrderItems(new HashSet<>());
        assertThat(order.getOrderItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();
    }

    @Test
    void discountTest() {
        Order order = getOrderRandomSampleGenerator();
        Discount discountBack = getDiscountRandomSampleGenerator();

        order.setDiscount(discountBack);
        assertThat(order.getDiscount()).isEqualTo(discountBack);

        order.discount(null);
        assertThat(order.getDiscount()).isNull();
    }
}
