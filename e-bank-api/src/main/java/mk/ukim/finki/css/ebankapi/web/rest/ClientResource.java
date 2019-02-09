package mk.ukim.finki.css.ebankapi.web.rest;

import mk.ukim.finki.css.ebankapi.model.DTO.TransactionDTO;
import mk.ukim.finki.css.ebankapi.model.exceptions.NotSufficientPermissionExpcetion;
import mk.ukim.finki.css.ebankapi.model.exceptions.UserDoesNotExistException;
import mk.ukim.finki.css.ebankapi.model.exceptions.UserNotLoggedInException;
import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import mk.ukim.finki.css.ebankapi.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping(path = "/clientHome")
public class ClientResource {

    private UsersService usersService;

    @Autowired
    public ClientResource (UsersService usersService) {
        this.usersService=usersService;
    }

    @PatchMapping(path = "/changePassword")
    public void changePassword(@RequestParam String newPassword,
                               @SessionAttribute String username)
            throws UserNotLoggedInException, UserDoesNotExistException {
        usersService.changeClientPassword(username,newPassword);
    }

    @GetMapping("/transacations")
    public List<TransactionDTO> getClientTransacations (@SessionAttribute String username)
            throws UserDoesNotExistException, UserNotLoggedInException {
        return usersService.getTransactionsByUser(usersService.getUserId(username));
    }

    @PostMapping("/new_transaction")
    public void makeNewTransaction(@RequestParam String recieverAccountNumber,
                                    @RequestParam Double amount,
                                    @RequestParam String password,
                                    @RequestParam Integer itemNumber,
                                    @RequestParam Long tokenNumber,
                                    HttpServletResponse response,
                                    @SessionAttribute String username)
            throws UserDoesNotExistException, NotSufficientPermissionExpcetion, UserNotLoggedInException {

        usersService.makeTransactions(
                usersService.getUserId(username),
                usersService.getUserIdByAccountNumber(recieverAccountNumber),
                null,
                amount,
                itemNumber,
                tokenNumber,
                password
        );

        response.setHeader("location","/transactions");

    }



}
