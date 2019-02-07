package mk.ukim.finki.css.ebankapi.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Account number is not valid!")
public class AccountNumberNotValidException extends Exception {

    private String accountNumber;

    public AccountNumberNotValidException(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public void message() {
        System.out.println("Account number "+ accountNumber + " is not valid!!");

    }
}
