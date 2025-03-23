package net.petcu.store.domain;

import static net.petcu.store.domain.DiscountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.petcu.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiscountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Discount.class);
        Discount discount1 = getDiscountSample1();
        Discount discount2 = new Discount();
        assertThat(discount1).isNotEqualTo(discount2);

        discount2.setId(discount1.getId());
        assertThat(discount1).isEqualTo(discount2);

        discount2 = getDiscountSample2();
        assertThat(discount1).isNotEqualTo(discount2);
    }
}
