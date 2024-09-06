package com.rookie.model.dto;

import com.rookie.model.entity.VideoTable;
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
    private VideoTable[] collections;
    private VideoTable[] favorites;
    private VideoTable[] uploads;

}
