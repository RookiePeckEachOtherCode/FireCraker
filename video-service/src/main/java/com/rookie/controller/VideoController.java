package com.rookie.controller;

import com.rookie.FileServiceClient;
import com.rookie.annotation.AuthRequired;
import com.rookie.aspect.Auth;
import com.rookie.model.FileBuckets;
import com.rookie.model.entity.VideoTable;
import com.rookie.model.result.BaseResult;
import com.rookie.model.result.None;
import com.rookie.service.VideoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/video")
public class VideoController {
    @Resource
    private VideoService videoService;
    @Resource
    private FileServiceClient fileServiceClient;


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


        var videoFileName = uid + "_" + UUID.randomUUID().toString() + "." + videoSuffix;
        var coverFileName = uid + "_" + UUID.randomUUID().toString() + "." + coverSuffix;

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
}
