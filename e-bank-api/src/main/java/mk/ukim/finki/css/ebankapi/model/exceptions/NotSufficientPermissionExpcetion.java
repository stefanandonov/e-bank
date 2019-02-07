package mk.ukim.finki.css.ebankapi.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Not sufficient permision. The user is not an employee")
public class NotSufficientPermissionExpcetion extends Throwable {
}
