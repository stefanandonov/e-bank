package mk.ukim.finki.css.ebankapi.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "user not found ")
public class UserDoesNotExistException extends Throwable {

    private String username;

    public UserDoesNotExistException(String username) {
        this.username=username;
    }

    public void message() {
        System.out.println("User with username" + username + "does not exist!");
    }
}
