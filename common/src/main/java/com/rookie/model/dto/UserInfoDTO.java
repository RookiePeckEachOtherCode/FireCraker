package com.rookie.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoDTO {
    private Long id;
    private String name;
    private String avatar;
    private String signature;
    private int updCnt;
    private int colCnt;
}
