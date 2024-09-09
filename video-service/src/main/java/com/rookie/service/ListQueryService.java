package com.rookie.service;

import com.rookie.model.dto.CommentInfo;
import com.rookie.model.dto.VideoSimpleInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListQueryService implements IListQueryService{
    @Override
    public List<CommentInfo> GetVideoComment(String vid,Integer offset,Integer size) {
        return List.of();
    }

    @Override
    public List<VideoSimpleInfo> GetUserFavVideo(String uid,Integer offset,Integer size) {
        return List.of();
    }

    @Override
    public List<VideoSimpleInfo> GetUserColVideo(String uid,Integer offset,Integer size) {
        return List.of();
    }

    @Override
    public List<VideoSimpleInfo> GetUserHistory(String uid,Integer offset,Integer size) {
        return List.of();
    }

    @Override
    public List<VideoSimpleInfo> SearchVideo(String keyword,Integer offset,Integer size) {
        return List.of();
    }

    @Override
    public List<VideoSimpleInfo> GetUserPublishedVideo(String uid,Integer offset,Integer size) {
        return List.of();
    }
}
