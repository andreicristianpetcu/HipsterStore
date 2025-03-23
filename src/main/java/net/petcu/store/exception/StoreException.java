package net.petcu.store.exception;

public abstract sealed class StoreException extends RuntimeException permits UserNotFoundException, UnauthorizedException {

    protected StoreException(String message) {
        super(message);
    }
}
