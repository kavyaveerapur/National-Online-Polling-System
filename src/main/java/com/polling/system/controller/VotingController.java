package com.polling.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.polling.system.model.Candidate;
import com.polling.system.model.User;
import com.polling.system.service.VotingService;

import jakarta.servlet.http.HttpSession;

@Controller
public class VotingController {

    @Autowired
    private VotingService service;

    // Show Candidates filtered by area/constituency (Requirement vi)
    @GetMapping("/vote")
    public String votePage(@RequestParam(value = "constituency", required = false) String constituency,
                           Model model,
                           HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        String targetConstituency = (constituency != null && !constituency.isEmpty()) ? constituency : user.getConstituency();
        
        model.addAttribute("candidates", service.getCandidatesByConstituency(targetConstituency));
        model.addAttribute("userConstituency", targetConstituency);
        return "vote";
    }

    // Submit Online Vote
    @PostMapping("/vote")
    public String vote(@RequestParam Long candidateId,
                       Model model,
                       HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        String message = service.vote(candidateId, user.getId());
        model.addAttribute("msg", message);
        return "result";
    }

    // Detailed Candidate Profiles constituency-wise (Requirement v)
    @GetMapping("/candidates")
    public String viewCandidateProfiles(@RequestParam(value = "constituency", required = false) String constituency, Model model) {
        model.addAttribute("candidates", service.getCandidatesByConstituency(constituency));
        model.addAttribute("selectedConstituency", constituency);
        return "candidates-profile";
    }

    // Candidate Self-Nomination Form (Requirement iii)
    @GetMapping("/candidate/nominate")
    public String nominateForm(Model model) {
        model.addAttribute("candidate", new Candidate());
        return "add-candidate";
    }

    // Submit Candidate Nomination
    @PostMapping("/candidate/nominate")
    public String nominateSubmit(@ModelAttribute Candidate candidate, @RequestParam("photo") MultipartFile photo) {
        service.submitNomination(candidate, photo);
        return "redirect:/?nominated=true";
    }

    // Admin / Election Commission Candidate Approval Dashboard (Requirement iii)
    @GetMapping("/admin/candidates")
    public String viewPendingCandidates(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("pendingCandidates", service.getPendingCandidates());
        return "admin-candidates";
    }

    @GetMapping("/admin/candidates/approve/{id}")
    public String approveCandidate(@PathVariable Long id) {
        service.approveCandidate(id);
        return "redirect:/admin/candidates";
    }

    @GetMapping("/admin/candidates/reject/{id}")
    public String rejectCandidate(@PathVariable Long id) {
        service.rejectCandidate(id);
        return "redirect:/admin/candidates";
    }

    // Administrator Manual Polling Results Entry (Requirement viii)
    @GetMapping("/admin/manual-results")
    public String manualResultsPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("candidates", service.getAllApprovedCandidates());
        return "admin-manual-results";
    }

    @PostMapping("/admin/manual-results")
    public String addManualResults(@RequestParam Long candidateId, @RequestParam int manualVotes) {
        service.addManualVotes(candidateId, manualVotes);
        return "redirect:/admin/manual-results?updated=true";
    }

    // Final Election Results & Statistics (Requirement vii & viii)
    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("results", service.getResults());
        model.addAttribute("winner", service.getWinner());
        return "results";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }
        return "admin-dashboard";
    }

    @GetMapping("/voter-dashboard")
    public String voterDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "voter-dashboard";
    }

    @GetMapping("/candidate-dashboard")
    public String candidateDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "candidate-dashboard";
    }
}
