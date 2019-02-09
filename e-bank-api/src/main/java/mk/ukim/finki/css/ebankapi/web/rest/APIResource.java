package mk.ukim.finki.css.ebankapi.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller()
public class APIResource {

    @GetMapping(value = "/login")
    public String hello() {
        return "index";
    }



}
