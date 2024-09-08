package com.rookie.model.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
public class UserListDTO {
    List<UserSimpleInfo> data;
}
