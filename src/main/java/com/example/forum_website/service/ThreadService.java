package com.example.forum_website.service;

import org.springframework.data.domain.Page;
import com.example.forum_website.model.Thread;

public interface ThreadService {
     public Page<Thread> getThreads(int page, int size);
}
