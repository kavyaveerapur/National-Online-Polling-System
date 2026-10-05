package com.polling.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.polling.system.model.VoterApplication;
import com.polling.system.model.User;
import com.polling.system.repository.VoterApplicationRepository;
import com.polling.system.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class VoterService {

    @Autowired
    private VoterApplicationRepository repo;

    @Autowired
    private UserRepository userRepo;

    private static final String UPLOAD_DIR = "uploads/voter-photos/";

    public VoterApplication apply(VoterApplication app, MultipartFile photo) {
        app.setStatus("PENDING");

        if (photo != null && !photo.isEmpty()) {
            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();

                String filename = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                app.setPhotoPath("/" + UPLOAD_DIR + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return repo.save(app);
    }

    public List<VoterApplication> getPending() {
        return repo.findByStatus("PENDING");
    }

    public List<VoterApplication> getAllApplications() {
        return repo.findAll();
    }

    // Field Officer Approval (Requirement i)
    public void approveByFieldOfficer(Long id, String fieldOfficerReport) {
        VoterApplication app = repo.findById(id).orElse(null);
        if (app != null) {
            app.setStatus("APPROVED");
            app.setFieldOfficerReport(fieldOfficerReport);
            
            // Generate unique Voter ID Card Number
            String newVoterId = "VID-" + System.currentTimeMillis() % 1000000;
            app.setGeneratedVoterId(newVoterId);

            // Update associated User
            User user = app.getUser();
            if (user != null) {
                user.setVerified(true);
                user.setVoterId(newVoterId);
                user.setConstituency(app.getConstituency());
                userRepo.save(user);
            }

            repo.save(app);
        }
    }

    public void rejectByFieldOfficer(Long id, String fieldOfficerReport) {
        VoterApplication app = repo.findById(id).orElse(null);
        if (app != null) {
            app.setStatus("REJECTED");
            app.setFieldOfficerReport(fieldOfficerReport);
            repo.save(app);
        }
    }
}
