package mk.ukim.finki.css.ebankapi.web.rest;


import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorControler {

    private final UsersService usersService;

    @Autowired
    public ErrorControler(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public String errorPage() {
        return "error.html";
    }




}
