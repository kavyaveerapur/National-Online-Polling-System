package com.polling.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.polling.system.model.Forum;
import com.polling.system.repository.ForumRepository;

@Controller
public class ForumController {

    @Autowired
    private ForumRepository repo;

    @GetMapping("/forum")
    public String forumPage(Model model) {
        model.addAttribute("messages", repo.findAll());
        model.addAttribute("msg", new Forum());
        return "forum";
    }

    @PostMapping("/forum")
    public String postMessage(@ModelAttribute Forum msg) {
        repo.save(msg);
        return "redirect:/forum";
    }
}