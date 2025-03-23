package net.petcu.store.service;

import net.petcu.store.domain.Order;

public interface PaymentService {
    boolean processPayment(Order order);
}
