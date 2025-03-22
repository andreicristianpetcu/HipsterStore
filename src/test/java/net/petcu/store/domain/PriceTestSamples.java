package net.petcu.store.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PriceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Price getPriceSample1() {
        return new Price().id(1L);
    }

    public static Price getPriceSample2() {
        return new Price().id(2L);
    }

    public static Price getPriceRandomSampleGenerator() {
        return new Price().id(longCount.incrementAndGet());
    }
}
