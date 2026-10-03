package com.polling.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.polling.system.model.Forum;
import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Long> {
    List<Forum> findAllByOrderByCreatedAtDesc();
    List<Forum> findByConstituencyOrderByCreatedAtDesc(String constituency);
}
