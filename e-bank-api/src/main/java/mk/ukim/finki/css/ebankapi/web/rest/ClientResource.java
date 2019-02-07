package mk.ukim.finki.css.ebankapi.web.rest;

import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import mk.ukim.finki.css.ebankapi.model.*;


@RestController
@RequestMapping(path = "/client")
public class ClientResource {

    private UsersService usersService;

    @Autowired
    public ClientResource (UsersService usersService) {
        this.usersService=usersService;
    }


}
