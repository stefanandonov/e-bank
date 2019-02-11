package mk.ukim.finki.css.ebankapi.web.rest;

import mk.ukim.finki.css.ebankapi.model.User;
import mk.ukim.finki.css.ebankapi.model.exceptions.*;
import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller()
@RequestMapping(path = "/employeeHome")
public class EmployeeResource {

    private UsersService usersService;

    public EmployeeResource(UsersService usersService) {

        this.usersService = usersService;
    }

    @GetMapping("/logout")
    public String logout (HttpServletRequest request,
                          @SessionAttribute String employeeUsername)
            throws UserDoesNotExistException, UserNotLoggedInException {
        usersService.logout(usersService.getUserId(employeeUsername));
        request.getSession().setAttribute("clientUsername",null);
        request.getSession().setAttribute("employeeUsername",null);
        return "index.html";
    }

    @GetMapping
    public String home_page (Model model,
                             @SessionAttribute String employeeUsername,
                             HttpServletRequest request) {
        model.addAttribute("employee_name", employeeUsername);
        request.getSession().setAttribute("result",null);
        return "employee_home.html";
    }

    @PostMapping
    public String home_page_post(Model model, @SessionAttribute String employeeUsername) {

        return null;
    }

    @PostMapping("/new_user")
    public void addNewUser(@RequestParam String name,
                             @RequestParam String username,
                             @RequestParam String accountNumber,
                             @RequestParam String address,
                             @RequestParam String telephone,
                             @RequestParam Double balance,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             @SessionAttribute String employeeUsername)
            throws UserNotLoggedInException, AccountNumberAlreadyExistsException, UserDoesNotExistException, AccountNumberNotValidException, UsernameAlreadyExistException, NotSufficientPermissionExpcetion {

        String result =  usersService.addClient(username,name,telephone,balance,
                accountNumber,address,usersService.getUserId(employeeUsername),new StringBuilder());

        response.setHeader("location","user_info");
        request.getSession().setAttribute("result",result);
        response.setStatus(302);

        //return result;
    }

    @GetMapping("/user_info")
    public String showUserInfo(Model model, @SessionAttribute String result) {
        model.addAttribute("result",result);
        return "user_info.html";
    }

    @GetMapping("/new_user")
    public String newUser() {
        return "employee_new_client.html";
    }

    @GetMapping("new_transaction")
    public String newTransaction() {
        return "employee_new_transaction.html";
    }

    @PostMapping("/new_transaction")
    public void makeNewTransaction(@RequestParam String senderAccountNumber,
                                    @RequestParam String receiverAccountNumber,
                                    @RequestParam Double amount,
                                    HttpServletResponse response,
                                    @SessionAttribute String employeeUsername)
            throws UserDoesNotExistException, NotSufficientPermissionExpcetion, UserNotLoggedInException, TokenDoesNotExistException {

        usersService.makeTransactions(
                usersService.getUserIdByAccountNumber(senderAccountNumber),
                usersService.getUserIdByAccountNumber(receiverAccountNumber),
                usersService.getUserId(employeeUsername),
                amount,null,null,null
        );

        response.setHeader("Location","employeeHome");
        response.setStatus(302);

    }
}
