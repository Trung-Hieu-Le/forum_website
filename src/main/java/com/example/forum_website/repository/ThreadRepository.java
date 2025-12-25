package com.example.forum_website.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.forum_website.model.Thread;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    Page<Thread> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @EntityGraph(attributePaths = {"user", "topic"})
    Page<Thread> findByGroupIdIsNullOrderByCreatedAtDesc(Pageable pageable);
}
