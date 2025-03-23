package net.petcu.store.exception;

public abstract sealed class StoreException
    extends RuntimeException
    permits OrderNotFoundException, UnauthorizedException, UserNotFoundException, ProductNotFoundException, PaymentFailedException {

    protected StoreException(String message) {
        super(message);
    }
}
