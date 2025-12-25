package com.example.forum_website.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.forum_website.dto.CreateThreadDto;
import com.example.forum_website.dto.UpdateThreadDto;
import com.example.forum_website.exception.ResourceNotFoundException;
import com.example.forum_website.model.Thread;
import com.example.forum_website.model.Topic;
import com.example.forum_website.model.User;
import com.example.forum_website.repository.ThreadRepository;
import com.example.forum_website.repository.TopicRepository;
import com.example.forum_website.service.ThreadService;
import com.example.forum_website.service.UserService;
import jakarta.transaction.Transactional;

@Service
public class ThreadServiceImpl implements ThreadService {
    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserService userService;

    @Override
    public Page<Thread> getThreads(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return threadRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Page<Thread> getPublicThreads(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return threadRepository.findByGroupIdIsNullOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Thread createThread(CreateThreadDto createThreadDto) {
        User currentUser = userService.getCurrentUser();
        Topic topic = topicRepository.findById(createThreadDto.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        Thread thread = new Thread();
        thread.setTitle(createThreadDto.getTitle());
        thread.setContent(createThreadDto.getContent() != null ? createThreadDto.getContent() : "");
        thread.setTopic(topic);
        thread.setUser(currentUser);
        thread.setGroupId(null); // Public post
        thread.setCreatedAt(LocalDateTime.now());
        thread.setUpdatedAt(LocalDateTime.now());

        return threadRepository.save(thread);
    }

    @Override
    @Transactional
    public Thread updateThread(Long threadId, UpdateThreadDto updateThreadDto) {
        User currentUser = userService.getCurrentUser();
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("Thread not found"));

        // Check if current user is the owner
        if (thread.getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("You don't have permission to edit this thread");
        }

        Topic topic = topicRepository.findById(updateThreadDto.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        thread.setTitle(updateThreadDto.getTitle());
        thread.setContent(updateThreadDto.getContent() != null ? updateThreadDto.getContent() : "");
        thread.setTopic(topic);
        thread.setUpdatedAt(LocalDateTime.now());

        return threadRepository.save(thread);
    }

    @Override
    public Thread getThreadById(Long threadId) {
        return threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("Thread not found"));
    }
}