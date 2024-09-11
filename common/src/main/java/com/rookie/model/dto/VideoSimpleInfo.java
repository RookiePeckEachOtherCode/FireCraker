package com.rookie.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoSimpleInfo {
    Long id;
    String play_cnt;
    String com_cnt;
    String fav_cnt;
    String cover_url;
    String title;
    String video_url;
}
