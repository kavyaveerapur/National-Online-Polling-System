package com.polling.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.polling.system.model.User;
import com.polling.system.model.VoterApplication;
import com.polling.system.service.VoterService;

import jakarta.servlet.http.HttpSession;

@Controller
public class VoterController {

    @Autowired
    private VoterService service;

    // Show Voter ID Application Form
    @GetMapping("/apply")
    public String applyPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("app", new VoterApplication());
        return "apply";
    }

    // Submit Voter ID Application with Photo (Requirement i)
    @PostMapping("/apply")
    public String apply(@ModelAttribute VoterApplication app,
                        @RequestParam("photo") MultipartFile photo,
                        HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        app.setUser(user);
        service.apply(app, photo);

        return "redirect:/voter-dashboard?applied=true";
    }

    // Field Officer Dashboard: View pending identity & address applications (Requirement i)
    @GetMapping("/field-officer/applications")
    public String viewFieldOfficerApplications(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"FIELD_OFFICER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("list", service.getPending());
        return "admin-applications";
    }

    // Field Officer Approval with report and generated Voter ID (Requirement i)
    @PostMapping("/field-officer/approve/{id}")
    public String approveFieldOfficer(@PathVariable Long id, @RequestParam("report") String report) {
        service.approveByFieldOfficer(id, report);
        return "redirect:/field-officer/applications";
    }

    // Field Officer Rejection
    @PostMapping("/field-officer/reject/{id}")
    public String rejectFieldOfficer(@PathVariable Long id, @RequestParam("report") String report) {
        service.rejectByFieldOfficer(id, report);
        return "redirect:/field-officer/applications";
    }
}
