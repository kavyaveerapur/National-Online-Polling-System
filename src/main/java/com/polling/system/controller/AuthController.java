package com.polling.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.polling.system.model.User;
import com.polling.system.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService service;

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {

        // 🔥 VALIDATION
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            model.addAttribute("error", "Email cannot be empty");
            return "register";
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("error", "Password cannot be empty");
            return "register";
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            model.addAttribute("error", "Name cannot be empty");
            return "register";
        }

        User saved = service.register(user);

        // 🔥 DUPLICATE EMAIL CHECK
        if (saved == null) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        jakarta.servlet.http.HttpSession session) {

        User user = service.login(email, password);

        if (user == null) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }

        // 🔥 STORE USER IN SESSION
        session.setAttribute("user", user);

        if (user.getRole().equals("ADMIN")) {
            return "admin-dashboard";
        }

        return "voter-dashboard";
    }
    
    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}