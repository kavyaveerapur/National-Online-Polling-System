package com.polling.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.polling.system.model.VoterApplication;
import java.util.List;
import java.util.Optional;

public interface VoterApplicationRepository extends JpaRepository<VoterApplication, Long> {
    List<VoterApplication> findByStatus(String status);
    Optional<VoterApplication> findByUserId(Long userId);
}
