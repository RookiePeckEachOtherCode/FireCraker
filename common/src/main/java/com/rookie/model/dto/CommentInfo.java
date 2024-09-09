package com.rookie.model.dto;


import lombok.Builder;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Builder
@Setter
public class CommentInfo {
    String id;
    String content;
    String fav_cnt;
    String avatar_url;
    String name;
    String uid;
    Boolean is_fav;
        
}
