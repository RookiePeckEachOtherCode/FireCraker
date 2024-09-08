package com.rookie.model;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UFavoriteMessage {
    private Long uid;
    private Long tid;
    private Boolean action;
}
