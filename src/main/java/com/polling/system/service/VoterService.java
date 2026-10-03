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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class VoterService {

    @Autowired
    private VoterApplicationRepository repo;

    @Autowired
    private UserRepository userRepo;

    private static final String UPLOAD_DIR = "uploads/voter-photos/";

    public VoterApplication apply(VoterApplication app, MultipartFile photoFile) {
        app.setStatus("PENDING");

        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = UUID.randomUUID().toString() + "_" + photoFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, photoFile.getBytes());
                app.setPhotoPath("/uploads/voter-photos/" + fileName);
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

    public VoterApplication getApplicationByUserId(Long userId) {
        return repo.findByUserId(userId).orElse(null);
    }

    // Field Officer Approval with Report & Generated Voter ID Card
    public void approve(Long id, String report) {
        VoterApplication app = repo.findById(id).orElse(null);
        if (app != null) {
            app.setStatus("APPROVED");
            app.setFieldOfficerReport(report != null && !report.trim().isEmpty() ? report : "Address & Identity validated by Field Officer.");

            // Generate unique Voter ID Card Number
            String generatedVoterId = "IND-" + (100000 + new Random().nextInt(900000));
            app.setGeneratedVoterId(generatedVoterId);

            // Update associated User
            User user = app.getUser();
            if (user != null) {
                user.setVerified(true);
                user.setVoterId(generatedVoterId);
                user.setConstituency(app.getConstituency());
                userRepo.save(user);
            }

            repo.save(app);
        }
    }

    // Field Officer Rejection
    public void reject(Long id, String report) {
        VoterApplication app = repo.findById(id).orElse(null);
        if (app != null) {
            app.setStatus("REJECTED");
            app.setFieldOfficerReport(report != null && !report.trim().isEmpty() ? report : "Application rejected after field validation.");
            repo.save(app);
        }
    }
}
