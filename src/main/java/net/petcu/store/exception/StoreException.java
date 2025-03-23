package net.petcu.store.exception;

public abstract sealed class StoreException
    extends RuntimeException
    permits
        OrderNotFoundException,
        UnauthorizedException,
        UserNotFoundException,
        ProductNotFoundException,
        PaymentFailedException,
        InvalidOrderStatusException,
        DiscountCodeNotFoundException {

    protected StoreException(String message) {
        super(message);
    }
}
