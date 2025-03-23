package net.petcu.store.exception;

public abstract sealed class StoreException
    extends RuntimeException
    permits OrderNotFoundException, UnauthorizedException, UserNotFoundException, ProductNotFoundException {

    protected StoreException(String message) {
        super(message);
    }
}
