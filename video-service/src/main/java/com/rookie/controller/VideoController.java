package com.rookie.controller;


import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.rookie.annotation.AuthRequired;
import com.rookie.aspect.Auth;
import com.rookie.model.dto.*;
import com.rookie.model.result.BaseResult;
import com.rookie.service.ListQueryService;
import com.rookie.service.VideoMainService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {
    @Resource
    VideoMainService videoMainService;
    @Resource
    ListQueryService listQueryService;
    
    @GetMapping("/recommend")
    public BaseResult<VideoSimpleInfoDTO> recommend(HttpServletRequest req) {
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        String uid = req.getParameter("uid");
        List<VideoSimpleInfo> simpleInfos = videoMainService.GetRecommendVideo(uid,offset,size);
        return BaseResult.success(VideoSimpleInfoDTO.builder().vlist(simpleInfos).build());
    }
    @GetMapping("/search")
    public BaseResult<VideoSimpleInfoDTO> search(HttpServletRequest req) {
        String keyword = req.getParameter("keyword");
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        List<VideoSimpleInfo> simpleInfos = listQueryService.SearchVideo(keyword,offset,size);
        return BaseResult.success(VideoSimpleInfoDTO.builder().vlist(simpleInfos).build());
    }
    @GetMapping("/user_fav")
    @AuthRequired
    public BaseResult<VideoSimpleInfoDTO> userFav(HttpServletRequest req) {
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        Long id = Auth.getToken().getId();
        List<VideoSimpleInfo> simpleInfos = listQueryService.GetUserFavVideo(id.toString(), offset, size);
        return BaseResult.success(VideoSimpleInfoDTO.builder().vlist(simpleInfos).build());
    }
    @GetMapping("/user_col")
    @AuthRequired
    public BaseResult<VideoSimpleInfoDTO> userCol(HttpServletRequest req) {
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        Long id = Auth.getToken().getId();
        List<VideoSimpleInfo> simpleInfos = listQueryService.GetUserColVideo(id.toString(), offset, size);
        return BaseResult.success(VideoSimpleInfoDTO.builder().vlist(simpleInfos).build());
    }
    @GetMapping("/user_pub")
    @AuthRequired
    public BaseResult<VideoSimpleInfoDTO> userPub(HttpServletRequest req) {
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        Long id = Auth.getToken().getId();
        List<VideoSimpleInfo> simpleInfos = listQueryService.GetUserPublishedVideo(id.toString(), offset, size);
        return BaseResult.success(VideoSimpleInfoDTO.builder().vlist(simpleInfos).build());
    }
    @GetMapping("/user_his")
    @AuthRequired
    public BaseResult<VideoSimpleInfoDTO> userHis(HttpServletRequest req) {
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        Long id = Auth.getToken().getId();
        List<VideoSimpleInfo> simpleInfos = listQueryService.GetUserHistory(id.toString(), offset, size);
        return BaseResult.success(VideoSimpleInfoDTO.builder().vlist(simpleInfos).build());
    }
    @GetMapping("/play")
    public BaseResult<VideoFullInfo> play(HttpServletRequest req) {
        String vid = req.getParameter("vid");
        String uid=req.getParameter("uid");
        VideoFullInfo videoFullInfo = videoMainService.GetVideoInfo(vid, uid);
        return BaseResult.success(videoFullInfo);
    }
    @GetMapping("/video_com")
    public BaseResult<VideoCommentsDTO> Comments(HttpServletRequest req) {
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        String vid = req.getParameter("vid");
        List<CommentInfo> commentInfos = listQueryService.GetVideoComment(vid, offset, size);
        return BaseResult.success(VideoCommentsDTO.builder().clist(commentInfos).build());
    }
    
}
