package com.polling.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.polling.system.model.User;
import com.polling.system.model.VoterApplication;
import com.polling.system.service.VoterService;

@Controller
public class VoterController {

    @Autowired
    private VoterService service;

    // Show application form
    @GetMapping("/apply")
    public String applyPage(Model model) {
        model.addAttribute("app", new VoterApplication());
        return "apply";
    }

    // Submit application
    @PostMapping("/apply")
    public String apply(@ModelAttribute VoterApplication app,
                        jakarta.servlet.http.HttpSession session) {

        User user = (User) session.getAttribute("user");

        // 🔥 THIS LINE WAS MISSING
        app.setUser(user);

        service.apply(app);

        return "redirect:/";
    }

    // Admin view pending applications
    @GetMapping("/admin/applications")
    public String viewApplications(Model model) {
        model.addAttribute("list", service.getPending());
        return "admin-applications";
    }

    // Approve
    @GetMapping("/admin/approve/{id}")
    public String approve(@PathVariable Long id) {
        service.approve(id);
        return "redirect:/admin/applications";
    }

    // Reject
    @GetMapping("/admin/reject/{id}")
    public String reject(@PathVariable Long id) {
        service.reject(id);
        return "redirect:/admin/applications";
    }
}