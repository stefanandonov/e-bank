package mk.ukim.finki.css.ebankapi.web.rest;

import mk.ukim.finki.css.ebankapi.model.User;
import mk.ukim.finki.css.ebankapi.model.enums.Role;
import mk.ukim.finki.css.ebankapi.model.exceptions.UserDoesNotExistException;
import mk.ukim.finki.css.ebankapi.model.exceptions.WrongPasswordException;
import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/login")
public class LoginResource {

    private final UsersService usersService;

    @Autowired
    public LoginResource (UsersService usersService) {
        this.usersService=usersService;
    }

    @PostMapping
    public void login (@RequestParam String username,
                       @RequestParam String password,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws UserDoesNotExistException, WrongPasswordException {

        User user = usersService.login(username,password);
        boolean status = user!=null;
        System.out.println(status);
        System.out.println(user.role);
        if (status) {
            if (user.role== Role.CLIENT){
                response.setHeader("Location", "/clientHome");
                request.getSession().setAttribute("clientUsername",username);
            }
            else {
                response.setHeader("Location", "/employeeHome");
                request.getSession().setAttribute("employeeUsername",username);
            }


            response.setStatus(302);
        }
        else {
            response.setHeader("Location", "/login");

        }
    }
}
