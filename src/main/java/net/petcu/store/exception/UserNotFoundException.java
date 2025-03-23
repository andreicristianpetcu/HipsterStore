package net.petcu.store.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
public final class UserNotFoundException extends StoreException {

    private final String userName;

    public UserNotFoundException(String message, String userName) {
        super(message + " " + userName);
        this.userName = userName;
    }
}
