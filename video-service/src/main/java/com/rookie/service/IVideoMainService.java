package com.rookie.service;

import com.rookie.model.dto.VideoFullInfo;
import com.rookie.model.dto.VideoSimpleInfo;

import java.util.List;

public interface IVideoMainService {
    List<VideoSimpleInfo> GetRecommendVideo(String uid,Integer offset,Integer size);
    VideoFullInfo GetVideoInfo(String vid,String uid);
}
