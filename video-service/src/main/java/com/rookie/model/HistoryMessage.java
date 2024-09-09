package com.rookie.model;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HistoryMessage {
    String uid;
    String vid;
}
