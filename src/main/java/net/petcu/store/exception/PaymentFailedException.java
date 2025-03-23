package net.petcu.store.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
@Getter
public final class PaymentFailedException extends StoreException {

    private final Double finalPrice;

    public PaymentFailedException(String message, Double finalPrice) {
        super(message);
        this.finalPrice = finalPrice;
    }
}
