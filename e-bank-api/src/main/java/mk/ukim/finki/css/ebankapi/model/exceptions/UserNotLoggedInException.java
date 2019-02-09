package mk.ukim.finki.css.ebankapi.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "the user is not logged in!")
public class UserNotLoggedInException extends Throwable {
}
