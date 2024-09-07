package com.rookie.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Message {
    private Long videoId;
    private Long userId;
    private Boolean action;

    @Override
    public String toString() {
        return "VideoCollectionMessage{" +
                "videoId=" + videoId +
                ", userId=" + userId +
                ", action=" + action +
                '}';
    }
}
