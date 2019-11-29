package me.mazaytsev.vkauth.controller;


import lombok.AllArgsConstructor;
import me.mazaytsev.vkauth.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class PagesController {

    private UserService userService;

    @RequestMapping("/")
    public String getIndex(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("name", userService.getUserAccountById(principal.getName()));
        }
        return "index";
    }
}