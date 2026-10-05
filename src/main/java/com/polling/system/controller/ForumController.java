package com.polling.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.polling.system.model.Forum;
import com.polling.system.model.User;
import com.polling.system.repository.ForumRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForumController {

    @Autowired
    private ForumRepository repo;

    // Campaign Forums and Stakeholder Discussions (Requirement iv & ix)
    @GetMapping("/forum")
    public String forumPage(@RequestParam(value = "constituency", required = false) String constituency,
                            Model model,
                            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        
        if (constituency != null && !constituency.isEmpty()) {
            model.addAttribute("messages", repo.findByConstituencyOrderByCreatedAtDesc(constituency));
        } else {
            model.addAttribute("messages", repo.findAllByOrderByCreatedAtDesc());
        }

        Forum msg = new Forum();
        if (user != null) {
            msg.setUsername(user.getName());
            msg.setUserRole(user.getRole());
            msg.setConstituency(user.getConstituency());
        }

        model.addAttribute("msg", msg);
        model.addAttribute("currentUser", user);
        return "forum";
    }

    @PostMapping("/forum")
    public String postMessage(@ModelAttribute Forum msg, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if (msg.getUsername() == null || msg.getUsername().isEmpty()) {
                msg.setUsername(user.getName());
            }
            msg.setUserRole(user.getRole());
            if (msg.getConstituency() == null || msg.getConstituency().isEmpty()) {
                msg.setConstituency(user.getConstituency());
            }
        }
        repo.save(msg);
        return "redirect:/forum";
    }
}
