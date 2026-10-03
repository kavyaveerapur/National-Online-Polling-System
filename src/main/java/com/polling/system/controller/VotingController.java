package com.polling.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.polling.system.model.Candidate;
import com.polling.system.model.User;
import com.polling.system.service.VotingService;

import jakarta.servlet.http.HttpSession;

@Controller
public class VotingController {

    @Autowired
    private VotingService service;

    // Show candidates
    @GetMapping("/vote")
    public String votePage(Model model,
                           jakarta.servlet.http.HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("candidates", service.getAllCandidates());
        return "vote";
    }

    // Submit vote
    @PostMapping("/vote")
    public String vote(@RequestParam Long candidateId,
                       Model model,
                       jakarta.servlet.http.HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        // 🔥 CHECK APPROVAL
        if (!user.isVerified()) {
            model.addAttribute("msg", "You are not approved to vote");
            return "result";
        }

        String message = service.vote(candidateId, user.getId());

        model.addAttribute("msg", message);

        return "result";
    }
    
    
    
    @GetMapping("/results")
    public String results(Model model) {

        model.addAttribute("results", service.getResults());
        model.addAttribute("winner", service.getWinner());

        return "results";
    }
    
    @GetMapping("/admin/add-candidate")
    public String addCandidatePage(Model model) {
        model.addAttribute("candidate", new Candidate());
        return "add-candidate";
    }

    @PostMapping("/admin/add-candidate")
    public String addCandidate(@ModelAttribute Candidate candidate) {
        service.addCandidate(candidate);
        return "redirect:/admin-dashboard";
    }
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/";
        }

        return "admin-dashboard";
    }
    @GetMapping("/voter-dashboard")
    public String voterDashboard() {
        return "voter-dashboard";
    }
}