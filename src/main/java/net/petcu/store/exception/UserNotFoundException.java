package net.petcu.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class UserNotFoundException extends StoreException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
