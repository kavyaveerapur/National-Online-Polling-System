package com.polling.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.polling.system.model.Candidate;
import com.polling.system.model.User;
import com.polling.system.repository.CandidateRepository;
import com.polling.system.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class VotingService {

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private UserRepository userRepo;

    private static final String UPLOAD_DIR = "uploads/candidate-photos/";

    public List<Candidate> getAllApprovedCandidates() {
        return candidateRepo.findByStatus("APPROVED");
    }

    public List<Candidate> getCandidatesByConstituency(String constituency) {
        if (constituency == null || constituency.trim().isEmpty()) {
            return candidateRepo.findByStatus("APPROVED");
        }
        return candidateRepo.findByConstituencyAndStatus(constituency, "APPROVED");
    }

    public List<Candidate> getPendingCandidates() {
        return candidateRepo.findByStatus("PENDING");
    }

    // Candidate Self-Nomination Registration (Requirement iii)
    public Candidate submitNomination(Candidate candidate, MultipartFile photo) {
        candidate.setStatus("PENDING");
        candidate.setOnlineVotes(0);
        candidate.setManualVotes(0);

        if (photo != null && !photo.isEmpty()) {
            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();

                String filename = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                candidate.setPhotoUrl("/" + UPLOAD_DIR + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return candidateRepo.save(candidate);
    }

    // Election Commission / Officer Validation (Requirement iii)
    public void approveCandidate(Long candidateId) {
        Candidate candidate = candidateRepo.findById(candidateId).orElse(null);
        if (candidate != null) {
            candidate.setStatus("APPROVED");
            candidateRepo.save(candidate);
        }
    }

    public void rejectCandidate(Long candidateId) {
        Candidate candidate = candidateRepo.findById(candidateId).orElse(null);
        if (candidate != null) {
            candidate.setStatus("REJECTED");
            candidateRepo.save(candidate);
        }
    }

    // Voting Logic (Requirement vi)
    public String vote(Long candidateId, Long userId) {
        User user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            return "User not found!";
        }

        if (!user.isVerified()) {
            return "You are not verified by Field Officer to vote!";
        }

        if (user.isVoted()) {
            return "You have already cast your vote!";
        }

        Candidate candidate = candidateRepo.findById(candidateId).orElse(null);
        if (candidate == null) {
            return "Candidate not found!";
        }

        candidate.setOnlineVotes(candidate.getOnlineVotes() + 1);
        user.setVoted(true);

        candidateRepo.save(candidate);
        userRepo.save(user);

        return "Vote successfully casted!";
    }

    // Administrator Manual Polling Results Entry (Requirement viii)
    public void addManualVotes(Long candidateId, int manualVotes) {
        Candidate candidate = candidateRepo.findById(candidateId).orElse(null);
        if (candidate != null) {
            candidate.setManualVotes(manualVotes);
            candidateRepo.save(candidate);
        }
    }

    public List<Candidate> getResults() {
        return candidateRepo.findAll();
    }

    public Candidate getWinner() {
        return candidateRepo.findAll()
                .stream()
                .max((c1, c2) -> Integer.compare(c1.getTotalVotes(), c2.getTotalVotes()))
                .orElse(null);
    }
}
