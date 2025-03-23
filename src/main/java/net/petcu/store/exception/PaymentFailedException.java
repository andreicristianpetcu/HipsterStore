package net.petcu.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public final class PaymentFailedException extends StoreException {

    public PaymentFailedException(String message) {
        super(message);
    }
}
