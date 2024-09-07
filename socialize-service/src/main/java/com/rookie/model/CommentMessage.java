package com.rookie.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentMessage {
    private Long vid;
    private Long uid;
    private String content;
}
