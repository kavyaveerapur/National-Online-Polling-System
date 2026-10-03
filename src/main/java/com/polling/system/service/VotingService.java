package com.polling.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polling.system.model.Candidate;
import com.polling.system.model.User;
import com.polling.system.repository.CandidateRepository;
import com.polling.system.repository.UserRepository;

import java.util.List;

@Service
public class VotingService {

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private UserRepository userRepo;

    public List<Candidate> getAllCandidates() {
        return candidateRepo.findAll();
    }

    public String vote(Long candidateId, Long userId) {

        User user = userRepo.findById(userId).get();

        if (user.isVoted()) {
            return "You already voted!";
        }

        Candidate candidate = candidateRepo.findById(candidateId).get();
        candidate.setVotes(candidate.getVotes() + 1);

        user.setVoted(true);

        candidateRepo.save(candidate);
        userRepo.save(user);

        return "Vote successful!";
    }

    // ✅ show all results
    public List<Candidate> getResults() {
        return candidateRepo.findAll();
    }

    // 🔥 winner logic
    public Candidate getWinner() {
        return candidateRepo.findAll()
                .stream()
                .max((c1, c2) -> c1.getVotes() - c2.getVotes())
                .orElse(null);
    }
    
    public Candidate addCandidate(Candidate candidate) {
        candidate.setVotes(0); // start with 0 votes
        return candidateRepo.save(candidate);
    }
}