package net.petcu.store.service.impl;

import java.util.Random;
import net.petcu.store.domain.Order;
import net.petcu.store.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class DummyPaymentService implements PaymentService {

    private final Random random = new Random();

    @Override
    public boolean processPayment(Order order) {
        // 80% chance of successful payment
        return random.nextDouble() < 0.8;
    }
}
