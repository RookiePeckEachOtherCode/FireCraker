package com.rookie.controller;

import com.alibaba.fastjson.JSONObject;
import com.rookie.annotation.AuthRequired;
import com.rookie.aspect.Auth;
import com.rookie.model.Message;
import com.rookie.model.result.BaseResult;
import com.rookie.model.result.None;
import com.rookie.service.VideoCollectionService;
import com.rookie.service.VideoCommentService;
import com.rookie.service.VideoFavoriteService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social")
public class SocializeController {
    @Resource
    KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private VideoCollectionService videoCollectionService;
    @Resource
    private VideoFavoriteService videoFavoriteService;
    @Resource
    private VideoCommentService videoCommentService;


    @PostMapping("/collect")
    @AuthRequired
    public BaseResult<None> collectVideo(HttpServletRequest req) {
        var uid = Auth.getToken().getId();
        var vid = Long.parseLong(req.getParameter("vid"));
        var action = Boolean.parseBoolean(req.getParameter("action"));

        var message = Message.builder()
                .userId(uid)
                .videoId(vid)
                .action(action)
                .build();


        kafkaTemplate.send("video-collection", JSONObject.toJSONString(message));
        return BaseResult.success();
    }
}
