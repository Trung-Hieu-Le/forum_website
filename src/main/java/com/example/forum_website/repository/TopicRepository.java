package com.example.forum_website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.forum_website.model.Topic;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findAllByOrderByNameAsc();
}


