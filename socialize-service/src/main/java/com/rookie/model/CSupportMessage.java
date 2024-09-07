package com.rookie.model;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CSupportMessage {
    private Long uid;
    private Long cid;
    private Boolean action;
}
