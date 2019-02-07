package mk.ukim.finki.css.ebankapi.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Username already exists!")
public class UsernameAlreadyExistException extends Exception {

    private String username;

    public UsernameAlreadyExistException(String username) {
        this.username=username;
    }

    public void message() {
        System.out.println("Username " + username + " already exists. Try a new one");
    }
}
