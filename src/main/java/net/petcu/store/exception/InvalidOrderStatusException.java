package net.petcu.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public final class InvalidOrderStatusException extends StoreException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
