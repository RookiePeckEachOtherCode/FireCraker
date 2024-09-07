package com.rookie.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.mybatisflex.core.query.QueryWrapper;
import com.rookie.annotation.AuthRequired;
import com.rookie.aspect.Auth;
import com.rookie.model.CSupportMessage;
import com.rookie.model.CommentMessage;
import com.rookie.model.Message;
import com.rookie.model.entity.table.CommentSupportTableTableDef;
import com.rookie.model.entity.table.VideoCommentTableTableDef;
import com.rookie.model.result.BaseResult;
import com.rookie.model.result.None;
import com.rookie.service.CommentSupportService;
import com.rookie.service.VideoCollectionService;
import com.rookie.service.VideoCommentService;
import com.rookie.service.VideoFavoriteService;
import com.rookie.utils.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static com.rookie.model.entity.table.CommentSupportTableTableDef.COMMENT_SUPPORT_TABLE;
import static com.rookie.model.entity.table.VideoCommentTableTableDef.VIDEO_COMMENT_TABLE;

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
    @Resource
    private CommentSupportService commentSupportService;
    @Resource
    private RedisUtils redisUtils;


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
    
    @PostMapping("/comment/add")
    @AuthRequired
    public BaseResult<None> addComment(HttpServletRequest req) {
        var uid= Auth.getToken().getId();
        var vid = Long.parseLong(req.getParameter("vid"));
        var content = req.getParameter("content");
        var message = CommentMessage.builder()
                .uid(uid)
                .vid(vid)
                .content(content);
        var future = kafkaTemplate.send("video-comment", JSONObject.toJSONString(message.build()));
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Messages cmp");
            }
        });
        return BaseResult.success();
    }
    
    @PostMapping("/comment/support")
    @AuthRequired
    public BaseResult<None> supportComment(HttpServletRequest req) {
        var uid= Auth.getToken().getId();
        var cid = Long.parseLong(req.getParameter("cid"));
        Boolean action = Boolean.parseBoolean(req.getParameter("action"));
        var message= CSupportMessage.builder()
                .uid(uid)
                .cid(cid)
                .action(action);
        kafkaTemplate.send("comment-support", JSONObject.toJSONString(message));
        return BaseResult.success();
    }
    
    @PostMapping("/comment/delete")
    @AuthRequired
    public BaseResult<None> deleteComment(HttpServletRequest req) {
        var cid = Long.parseLong(req.getParameter("cid"));
        commentSupportService.remove(QueryWrapper.create()
                .where(COMMENT_SUPPORT_TABLE.CID.eq(cid)));
        videoCommentService.remove(QueryWrapper.create()
                .where(VIDEO_COMMENT_TABLE.ID.eq(cid)));
        redisUtils.deleteValue("comment-support" + cid);
        return BaseResult.success();
    }
}
