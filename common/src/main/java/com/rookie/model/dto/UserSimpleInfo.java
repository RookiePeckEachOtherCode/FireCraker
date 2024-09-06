package com.rookie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class UserSimpleInfo {
    String id;
    String avatar;
    String name;
    String signature;

}
