package net.petcu.store.domain;

import static net.petcu.store.domain.PriceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.petcu.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PriceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Price.class);
        Price price1 = getPriceSample1();
        Price price2 = new Price();
        assertThat(price1).isNotEqualTo(price2);

        price2.setId(price1.getId());
        assertThat(price1).isEqualTo(price2);

        price2 = getPriceSample2();
        assertThat(price1).isNotEqualTo(price2);
    }
}
