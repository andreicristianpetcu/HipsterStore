package net.petcu.store.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DiscountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Discount getDiscountSample1() {
        return new Discount().id(1L).discountCode(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Discount getDiscountSample2() {
        return new Discount().id(2L).discountCode(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Discount getDiscountRandomSampleGenerator() {
        return new Discount().id(longCount.incrementAndGet()).discountCode(UUID.randomUUID());
    }
}
