package mk.ukim.finki.css.ebankapi.web.rest;

import mk.ukim.finki.css.ebankapi.model.User;
import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
