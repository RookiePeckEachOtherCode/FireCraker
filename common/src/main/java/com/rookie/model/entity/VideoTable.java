package com.rookie.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("tb_video")
public class VideoTable {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;
    private Long uid;
    private String title;
    private String description;
    private String videoUrl;
    private String coverUrl;
    private Long createTime;
    private Long updateTime;
    private Integer fav_cnt;
    private Integer play_cnt;
    private Integer col_cnt;
}
