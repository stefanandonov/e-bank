package mk.ukim.finki.css.ebankapi.model.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "token does not exist!")
public class TokenDoesNotExistException extends Throwable {
}
