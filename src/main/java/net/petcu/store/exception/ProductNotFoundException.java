package net.petcu.store.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a product cannot be found or has no active price.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
public final class ProductNotFoundException extends StoreException {

    private final Long productId;

    public ProductNotFoundException(String messagePrefix, Long productId) {
        super(messagePrefix + productId);
        this.productId = productId;
    }
}
