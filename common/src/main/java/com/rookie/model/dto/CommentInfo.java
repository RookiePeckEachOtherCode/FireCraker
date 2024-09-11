package com.rookie.model.dto;


import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class CommentInfo {
    Long id;
    String content;
    String fav_cnt;
    String avatar_url;
    String name;
    Long uid;
    Boolean is_fav;

}
