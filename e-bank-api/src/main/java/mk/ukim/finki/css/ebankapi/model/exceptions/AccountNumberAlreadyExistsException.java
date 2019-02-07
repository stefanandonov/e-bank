package mk.ukim.finki.css.ebankapi.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value =  HttpStatus.BAD_REQUEST, reason = "Account number already exists")
public class AccountNumberAlreadyExistsException extends Throwable {

    String accountNumber;
    public AccountNumberAlreadyExistsException(String accountNumber) {
        this.accountNumber=accountNumber;
    }

    public void message() {
        System.out.println("Account number "+accountNumber+" already exists in the system!");
    }
}
