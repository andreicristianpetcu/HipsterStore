package net.petcu.store.domain;

import static net.petcu.store.domain.OrderItemTestSamples.*;
import static net.petcu.store.domain.PriceTestSamples.*;
import static net.petcu.store.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.petcu.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItem.class);
        OrderItem orderItem1 = getOrderItemSample1();
        OrderItem orderItem2 = new OrderItem();
        assertThat(orderItem1).isNotEqualTo(orderItem2);

        orderItem2.setId(orderItem1.getId());
        assertThat(orderItem1).isEqualTo(orderItem2);

        orderItem2 = getOrderItemSample2();
        assertThat(orderItem1).isNotEqualTo(orderItem2);
    }

    @Test
    void productTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        orderItem.setProduct(productBack);
        assertThat(orderItem.getProduct()).isEqualTo(productBack);

        orderItem.product(null);
        assertThat(orderItem.getProduct()).isNull();
    }

    @Test
    void priceTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        Price priceBack = getPriceRandomSampleGenerator();

        orderItem.setPrice(priceBack);
        assertThat(orderItem.getPrice()).isEqualTo(priceBack);

        orderItem.price(null);
        assertThat(orderItem.getPrice()).isNull();
    }
}
