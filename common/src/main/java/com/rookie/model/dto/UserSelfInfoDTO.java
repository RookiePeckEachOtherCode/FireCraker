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
    private Long favCnt;
    private Long falCnt;
    private Long fedCnt;
    private Long colCnt;
    private Long updCnt;
    private boolean showCollection;
}
