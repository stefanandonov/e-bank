package mk.ukim.finki.css.ebankapi.web.rest;

import mk.ukim.finki.css.ebankapi.model.DTO.TransactionDTO;
import mk.ukim.finki.css.ebankapi.model.exceptions.NotSufficientPermissionExpcetion;
import mk.ukim.finki.css.ebankapi.model.exceptions.TokenDoesNotExistException;
import mk.ukim.finki.css.ebankapi.model.exceptions.UserDoesNotExistException;
import mk.ukim.finki.css.ebankapi.model.exceptions.UserNotLoggedInException;
import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import mk.ukim.finki.css.ebankapi.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping(path = "/clientHome")
public class ClientResource {

    private UsersService usersService;

    @Autowired
    public ClientResource (UsersService usersService) {
        this.usersService=usersService;
    }

    @GetMapping
    public String showHomePage(Model model, @SessionAttribute String clientUsername) {
        model.addAttribute("username",clientUsername);
        return "clientHome.html";
    }

    @GetMapping("/logout")
    public String logout (HttpServletRequest request,
                          @SessionAttribute String clientUsername)
            throws UserDoesNotExistException, UserNotLoggedInException {
        usersService.logout(usersService.getUserId(clientUsername));
        request.getSession().setAttribute("clientUsername",null);
        request.getSession().setAttribute("employeeUsername",null);
        return "index.html";
    }

    @GetMapping("/changePassword")
    public String showChangePasswordPage () {
        return "changePassword.html";
    }

    /*@GetMapping("/transactions")
    public String showTransactionsPage() {
        return "client_transactions.html";
    }*/

    @GetMapping("/new_transaction")
    public String showNewTransactionPage(Model model,
                                         @SessionAttribute String clientUsername)
            throws UserDoesNotExistException {

        model.addAttribute("username",clientUsername);
        model.addAttribute("tokenItemNumber",
                usersService.chooseRandomTokenForVerification(usersService.getUserId(clientUsername)));
        return "client_new_transaction.html";

    }

    @PostMapping(path = "/changePassword")
    public void changePassword(@RequestParam String newPassword,
                               @SessionAttribute String clientUsername,
                               HttpServletResponse response)
            throws UserNotLoggedInException, UserDoesNotExistException {
        usersService.changeClientPassword(clientUsername,newPassword);

        response.setHeader("Location","/clientHome");
        response.setStatus(302);

    }

    @GetMapping("/transactions")
    public String getClientTransacations
            (@SessionAttribute String clientUsername,
             Model model)
            throws UserDoesNotExistException, UserNotLoggedInException {
        model.addAttribute("transactions",
                usersService.getTransactionsByUser
                        (usersService.getUserId(clientUsername)));
        model.addAttribute("username", clientUsername);
        model.addAttribute("balance", usersService.balance(clientUsername));
        return "transactions_list.html";
    }

    @PostMapping("/new_transaction")
    public void makeNewTransaction(@RequestParam String recieverAccountNumber,
                                    @RequestParam Double amount,
                                    @RequestParam String password,
                                    @RequestParam Integer itemNumber,
                                    @RequestParam Long tokenNumber,
                                    HttpServletResponse response,
                                    @SessionAttribute String clientUsername)
            throws UserDoesNotExistException, NotSufficientPermissionExpcetion, UserNotLoggedInException, TokenDoesNotExistException {

        usersService.makeTransactions(
                usersService.getUserId(clientUsername),
                usersService.getUserIdByAccountNumber(recieverAccountNumber),
                null,
                amount,
                itemNumber,
                tokenNumber,
                password
        );

        response.setHeader("location","transactions");
        response.setStatus(302);

    }



}
