package com.rookie.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserLoginDTO {
    private String token;
}
