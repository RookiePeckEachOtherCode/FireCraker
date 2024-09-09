package com.rookie.controller;

import com.rookie.FileServiceClient;
import com.rookie.annotation.AuthRequired;
import com.rookie.aspect.Auth;
import com.rookie.model.FileBuckets;
import com.rookie.model.dto.*;
import com.rookie.model.entity.VideoTable;
import com.rookie.model.result.BaseResult;
import com.rookie.model.result.None;
import com.rookie.service.ListQueryService;
import com.rookie.service.VideoMainService;
import com.rookie.service.VideoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/video")
public class VideoController {
    @Resource
    private VideoService videoService;
    @Resource
    private FileServiceClient fileServiceClient;

    @Resource
    private VideoMainService videoMainService;

    @Resource
    private ListQueryService listQueryService;

    @PostMapping("/upload")
    @AuthRequired
    public BaseResult<None> uploadVideo(
            MultipartFile video,
            MultipartFile cover,
            HttpServletRequest req
    ) {
        var uid = Auth.getToken().getId();
        var title = req.getParameter("title");
        var description = req.getParameter("description");
        var tags = req.getParameter("tags");
        var token = req.getParameter("token");

        var oVideoFileName = video.getOriginalFilename();
        var oCoverFileName = cover.getOriginalFilename();

        var videoSuffix = oVideoFileName != null ? oVideoFileName.substring(oVideoFileName.lastIndexOf(".") + 1) : "mp4";
        var coverSuffix = oCoverFileName != null ? oCoverFileName.substring(oCoverFileName.lastIndexOf(".") + 1) : "png";


        var videoFileName = uid + "_" + UUID.randomUUID() + "." + videoSuffix;
        var coverFileName = uid + "_" + UUID.randomUUID() + "." + coverSuffix;

        var uploadVideoResult = fileServiceClient.uploadVideo(video, videoFileName, token);
        if (!uploadVideoResult.ok()) {
            return BaseResult.fail("upload video failed");
        }


        var uploadCoverResult = fileServiceClient.uploadImage(cover, coverFileName, FileBuckets.COVER_BUCKET.getBucketName(), token);
        if (!uploadVideoResult.ok()) {
            return BaseResult.fail("upload cover failed");
        }


        var videoTable = VideoTable.builder()
                .uid(uid)
                .title(title)
                .description(description)
                .tags(tags)
                .videoUrl(uploadVideoResult.getData().getFileUrl())
                .coverUrl(uploadCoverResult.getData().getFileUrl())
                .createTime(System.currentTimeMillis())
                .updateTime(System.currentTimeMillis())
                .build();

        videoService.save(videoTable);

        return BaseResult.success();
    }


    @GetMapping("/recommend")
    public BaseResult<VideoSimpleInfoDTO> recommend(HttpServletRequest req) {
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        String uid = req.getParameter("uid");
        List<VideoSimpleInfo> simpleInfos = videoMainService.GetRecommendVideo(uid, offset, size);
        return BaseResult.success(VideoSimpleInfoDTO.builder().vlist(simpleInfos).build());
    }

    @GetMapping("/search")
    public BaseResult<VideoSimpleInfoDTO> search(HttpServletRequest req) {
        String keyword = req.getParameter("keyword");
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        List<VideoSimpleInfo> simpleInfos = listQueryService.SearchVideo(keyword, offset, size);
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
        String uid = req.getParameter("uid");
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
