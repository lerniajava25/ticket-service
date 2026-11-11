package org.example.ticketservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Nu ska den leta efter din login.html igen
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
}
