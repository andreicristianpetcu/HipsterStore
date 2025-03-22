package net.petcu.store.domain;

import static net.petcu.store.domain.PriceTestSamples.*;
import static net.petcu.store.domain.PricedProductTestSamples.*;
import static net.petcu.store.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.petcu.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PricedProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PricedProduct.class);
        PricedProduct pricedProduct1 = getPricedProductSample1();
        PricedProduct pricedProduct2 = new PricedProduct();
        assertThat(pricedProduct1).isNotEqualTo(pricedProduct2);

        pricedProduct2.setId(pricedProduct1.getId());
        assertThat(pricedProduct1).isEqualTo(pricedProduct2);

        pricedProduct2 = getPricedProductSample2();
        assertThat(pricedProduct1).isNotEqualTo(pricedProduct2);
    }

    @Test
    void productTest() {
        PricedProduct pricedProduct = getPricedProductRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        pricedProduct.setProduct(productBack);
        assertThat(pricedProduct.getProduct()).isEqualTo(productBack);

        pricedProduct.product(null);
        assertThat(pricedProduct.getProduct()).isNull();
    }

    @Test
    void priceTest() {
        PricedProduct pricedProduct = getPricedProductRandomSampleGenerator();
        Price priceBack = getPriceRandomSampleGenerator();

        pricedProduct.setPrice(priceBack);
        assertThat(pricedProduct.getPrice()).isEqualTo(priceBack);

        pricedProduct.price(null);
        assertThat(pricedProduct.getPrice()).isNull();
    }
}
