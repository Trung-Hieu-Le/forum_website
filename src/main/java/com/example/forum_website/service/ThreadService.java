package com.example.forum_website.service;

import org.springframework.data.domain.Page;
import com.example.forum_website.dto.CreateThreadDto;
import com.example.forum_website.dto.UpdateThreadDto;
import com.example.forum_website.model.Thread;

public interface ThreadService {
     public Page<Thread> getThreads(int page, int size);
     public Page<Thread> getPublicThreads(int page, int size);
     public Thread createThread(CreateThreadDto createThreadDto);
     public Thread updateThread(Long threadId, UpdateThreadDto updateThreadDto);
     public Thread getThreadById(Long threadId);
}
