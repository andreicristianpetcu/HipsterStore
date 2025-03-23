package net.petcu.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a product cannot be found or has no active price.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public final class ProductNotFoundException extends StoreException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
