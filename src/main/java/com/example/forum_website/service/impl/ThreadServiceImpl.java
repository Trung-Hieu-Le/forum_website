package com.example.forum_website.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.forum_website.repository.ThreadRepository;
import com.example.forum_website.service.ThreadService;
import com.example.forum_website.model.Thread;

@Service
public class ThreadServiceImpl implements ThreadService {
    @Autowired
    private ThreadRepository threadRepository;

    @Override
    public Page<Thread> getThreads(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return threadRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}