package com.example.GridFSDemo.controller;

import com.example.GridFSDemo.model.User;
import com.example.GridFSDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        // Check if username already exists
        if (userService.findByUsername(user.getUsername()) != null) {
            return "redirect:/register?error";
        }

        userService.registerNewUser(user);
        return "redirect:/login?registered";
    }
}