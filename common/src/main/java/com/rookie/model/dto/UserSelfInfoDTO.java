package com.rookie.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSelfInfoDTO {
    private Long id;
    private String name;
    private String avatar;
    private String signature;
    private int favCnt;
    private int colCnt;
    private int updCnt;
    private boolean showCollection;
}
