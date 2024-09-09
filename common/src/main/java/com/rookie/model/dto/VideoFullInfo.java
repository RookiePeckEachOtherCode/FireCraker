package com.rookie.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class VideoFullInfo {
    String id;
    String  video_url;
    String cover_url;
    String description;
    Long update_time;
    Integer play_cnt;
    Integer fav_cnt;
    Integer col_cnt;
    Boolean is_fav;
    Boolean is_col;
    String title;
    String uid;
}
