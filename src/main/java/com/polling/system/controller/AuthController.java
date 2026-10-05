package com.polling.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.polling.system.model.User;
import com.polling.system.service.UserService;
import com.polling.system.service.OtpService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

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
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            model.addAttribute("error", "Name cannot be empty");
            return "register";
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "Password cannot be empty");
            return "register";
        }

        User saved = userService.register(user);

        if (saved == null) {
            model.addAttribute("error", "Email or Voter ID already exists");
            return "register";
        }

        return "redirect:/?registered=true";
    }

    // Smart Universal Dashboard Redirect Route
    @GetMapping("/dashboard")
    public String universalDashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin-dashboard";
        } else if ("FIELD_OFFICER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/field-officer/applications";
        } else if ("CANDIDATE".equalsIgnoreCase(user.getRole())) {
            return "redirect:/candidate-dashboard";
        }

        return "redirect:/voter-dashboard";
    }

    // Step 1: Initial Login Verification
    @PostMapping("/login")
    public String login(@RequestParam String loginIdentifier,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {

        User user = userService.login(loginIdentifier, password);

        if (user == null) {
            model.addAttribute("error", "Invalid Credentials or Voter ID");
            return "login";
        }

        // Store pre-authenticated user in session pending OTP check
        session.setAttribute("pendingUser", user);
        
        // Generate 6-digit OTP
        String identifier = (user.getVoterId() != null && !user.getVoterId().isEmpty()) ? user.getVoterId() : user.getEmail();
        otpService.generateAndStoreOtp(session, identifier);

        return "redirect:/verify-otp";
    }

    // Step 2: Render OTP Verification Screen
    @GetMapping("/verify-otp")
    public String verifyOtpPage(Model model, HttpSession session, @RequestParam(value = "resent", required = false) String resent) {
        User pendingUser = (User) session.getAttribute("pendingUser");
        if (pendingUser == null) {
            return "redirect:/";
        }

        String currentOtp = otpService.getCurrentOtp(session);
        
        model.addAttribute("pendingUser", pendingUser);
        model.addAttribute("demoOtp", currentOtp);
        if ("true".equals(resent)) {
            model.addAttribute("msg", "A new OTP has been generated and sent!");
        }

        return "verify-otp";
    }

    // Step 3: Validate 6-Digit OTP Code
    @PostMapping("/verify-otp")
    public String processOtpVerification(@RequestParam("otp") String otp,
                                         Model model,
                                         HttpSession session) {

        User pendingUser = (User) session.getAttribute("pendingUser");
        if (pendingUser == null) {
            return "redirect:/";
        }

        boolean isValid = otpService.validateOtp(session, otp);

        if (!isValid) {
            model.addAttribute("pendingUser", pendingUser);
            model.addAttribute("demoOtp", otpService.getCurrentOtp(session));
            model.addAttribute("error", "Invalid or expired OTP code. Please enter the correct 6-digit code.");
            return "verify-otp";
        }

        // OTP Verified! Promote user to full authenticated session
        session.removeAttribute("pendingUser");
        session.setAttribute("user", pendingUser);

        return universalDashboard(session);
    }

    // Resend OTP Code
    @GetMapping("/resend-otp")
    public String resendOtp(HttpSession session) {
        User pendingUser = (User) session.getAttribute("pendingUser");
        if (pendingUser != null) {
            String identifier = (pendingUser.getVoterId() != null && !pendingUser.getVoterId().isEmpty()) ? pendingUser.getVoterId() : pendingUser.getEmail();
            otpService.generateAndStoreOtp(session, identifier);
            return "redirect:/verify-otp?resent=true";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
