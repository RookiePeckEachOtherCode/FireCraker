package com.rookie.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoSimpleInfo {
    String id;
    String play_cnt;
    String com_cnt;
    String fav_cnt;
    String cover_url;
    String title;
    String video_url;
}
