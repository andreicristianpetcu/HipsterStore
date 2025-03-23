package net.petcu.store.exception;

import lombok.Getter;
import net.petcu.store.domain.enumeration.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Getter
public final class InvalidOrderStatusException extends StoreException {

    private final OrderStatus orderStatus;
    private final Long orderId;

    public InvalidOrderStatusException(String message, OrderStatus orderStatus, Long orderId) {
        super(message);
        this.orderStatus = orderStatus;
        this.orderId = orderId;
    }
}
