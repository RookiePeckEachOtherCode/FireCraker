package com.rookie.model.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
public class UserFavoriteUserListDTO {
    List<UserSimpleInfo> ulist;
}
