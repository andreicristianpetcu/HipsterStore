package net.petcu.store.exception;

public abstract sealed class StoreException
    extends RuntimeException
    permits OrderNotFoundException, UnauthorizedException, UserNotFoundException {

    protected StoreException(String message) {
        super(message);
    }
}
