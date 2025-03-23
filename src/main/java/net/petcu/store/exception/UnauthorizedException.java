package net.petcu.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public final class UnauthorizedException extends StoreException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
