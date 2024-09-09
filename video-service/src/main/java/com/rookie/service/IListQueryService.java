package com.rookie.service;

import com.rookie.model.dto.CommentInfo;
import com.rookie.model.dto.VideoSimpleInfo;

import java.util.List;

public interface IListQueryService {
    List<CommentInfo> GetVideoComment(String vid,Integer offset,Integer size);
    List<VideoSimpleInfo> GetUserFavVideo(String uid,Integer offset,Integer size);
    List<VideoSimpleInfo> GetUserColVideo(String uid,Integer offset,Integer size);
    List<VideoSimpleInfo> GetUserHistory(String uid,Integer offset,Integer size);
    List<VideoSimpleInfo> SearchVideo(String keyword,Integer offset,Integer size);
    List<VideoSimpleInfo> GetUserPublishedVideo(String uid,Integer offset,Integer size);
}
