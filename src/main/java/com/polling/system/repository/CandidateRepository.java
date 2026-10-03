package com.polling.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.polling.system.model.Candidate;
import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByStatus(String status);
    List<Candidate> findByConstituencyAndStatus(String constituency, String status);
    List<Candidate> findByConstituency(String constituency);
}
