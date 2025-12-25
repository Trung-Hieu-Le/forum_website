package com.example.forum_website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateThreadDto {
    @NotBlank(message = "{thread.title.notBlank}")
    private String title;

    private String content;

    @NotNull(message = "{thread.topic.notNull}")
    private Long topicId;
}

