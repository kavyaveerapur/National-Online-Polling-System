package com.polling.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.polling.system.model.User;
import com.polling.system.repository.UserRepository;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public User register(User user) {
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            User existingEmail = repo.findByEmail(user.getEmail());
            if (existingEmail != null) {
                return null;
            }
        }
        
        // If registering with existing valid Voter ID (Requirement ii)
        if (user.getVoterId() != null && !user.getVoterId().trim().isEmpty()) {
            User existingVoterId = repo.findByVoterId(user.getVoterId());
            if (existingVoterId != null) {
                return null;
            }
            user.setVerified(true); // Pre-existing voter card holders auto-verified
        }

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("VOTER");
        }

        return repo.save(user);
    }

    public User login(String loginIdentifier, String password) {
        User user = repo.findByEmail(loginIdentifier);
        if (user == null) {
            user = repo.findByVoterId(loginIdentifier);
        }

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public List<User> getUsersByRole(String role) {
        return repo.findAll().stream()
                .filter(u -> role.equalsIgnoreCase(u.getRole()))
                .toList();
    }
}
