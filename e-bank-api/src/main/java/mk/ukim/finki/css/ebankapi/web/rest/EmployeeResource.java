package mk.ukim.finki.css.ebankapi.web.rest;

import mk.ukim.finki.css.ebankapi.model.User;
import mk.ukim.finki.css.ebankapi.model.exceptions.*;
import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(path = "/employee")
public class EmployeeResource {

    private UsersService usersService;

    public EmployeeResource(UsersService usersService) {
        this.usersService = usersService;
    }


    @GetMapping
    public List<User> getClients () {
        return null;
    }

    @PostMapping("/new_user")
    public String addNewUser(@RequestParam String name,
                             @RequestParam String username,
                             @RequestParam String accountNumber,
                             @RequestParam String address,
                             @RequestParam String telephone,
                             @RequestParam Double balance,
                             @RequestParam String email,
                             HttpServletRequest request,
                             HttpServletResponse response ,
                             @SessionAttribute String employeeUsername) throws UserNotLoggedInException, AccountNumberAlreadyExistsException, UserDoesNotExistException, AccountNumberNotValidException, UsernameAlreadyExistException, NotSufficientPermissionExpcetion {

        String result =  usersService.addClient(username,name,telephone,balance,
                accountNumber,address,usersService.getUserId(employeeUsername),new StringBuilder());

        response.setHeader("location","/user_info");
        request.getSession().setAttribute("result",result);

        return result;
    }

    @PostMapping("/new_transaction")
    public void makeNewTransaction(@RequestParam String senderAccountNumber,
                                    @RequestParam String receiverAccountNumber,
                                    @RequestParam Double amount,
                                    HttpServletResponse response,
                                    @SessionAttribute String username)
            throws UserDoesNotExistException, NotSufficientPermissionExpcetion, UserNotLoggedInException {

        usersService.makeTransactions(
                usersService.getUserIdByAccountNumber(senderAccountNumber),
                usersService.getUserIdByAccountNumber(receiverAccountNumber),
                usersService.getUserId(username),
                amount,null,null,null
        );

    }
}
