package net.petcu.store.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PricedProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PricedProduct getPricedProductSample1() {
        return new PricedProduct().id(1L);
    }

    public static PricedProduct getPricedProductSample2() {
        return new PricedProduct().id(2L);
    }

    public static PricedProduct getPricedProductRandomSampleGenerator() {
        return new PricedProduct().id(longCount.incrementAndGet());
    }
}
