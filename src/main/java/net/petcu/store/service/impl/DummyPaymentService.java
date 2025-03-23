package net.petcu.store.service.impl;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import net.petcu.store.domain.Order;
import net.petcu.store.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DummyPaymentService implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(DummyPaymentService.class);
    private final RandomGenerator randomGenerator = RandomGeneratorFactory.of("Random").create();

    @Override
    public boolean processPayment(Order order) {
        // 80% chance of successful payment
        boolean success = randomGenerator.nextDouble() < 0.8;
        log.debug("Payment processing for order {}: {}", order.getId(), success ? "success" : "insufficient funds");
        return success;
    }
}
