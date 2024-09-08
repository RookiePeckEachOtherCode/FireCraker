package com.rookie.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.mybatisflex.core.query.QueryWrapper;
import com.rookie.annotation.AuthRequired;
import com.rookie.aspect.Auth;
import com.rookie.consts.RedisKey;
import com.rookie.model.CSupportMessage;
import com.rookie.model.CommentMessage;
import com.rookie.model.Message;
import com.rookie.model.UFavoriteMessage;
import com.rookie.model.entity.VideoCommentTable;
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
        var action =req.getParameter("action").equals("1");

        var message = Message.builder()
                .userId(uid)
                .videoId(vid)
                .action(action)
                .build();


        kafkaTemplate.send("video-collection", JSONObject.toJSONString(message));
        return BaseResult.success();
    }
    @PostMapping("/like")
    @AuthRequired
    public BaseResult<None> likeVideo(HttpServletRequest req) {
        var uid = Auth.getToken().getId();
        var vid = Long.parseLong(req.getParameter("vid"));
        var action =req.getParameter("action").equals("1");
        Message message = Message.builder()
                .userId(uid)
                .videoId(vid)
                .action(action)
                .build();
        
        kafkaTemplate.send("video-favorite", JSONObject.toJSONString(message));
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
        var action = req.getParameter("action").equals("1");
        var message= CSupportMessage.builder()
                .uid(uid)
                .cid(cid)
                .action(action);
        kafkaTemplate.send("comment-support", JSONObject.toJSONString(message.build()));
        return BaseResult.success();
    }
    
    @PostMapping("/comment/delete")
    @AuthRequired
    public BaseResult<None> deleteComment(HttpServletRequest req) {
        var cid = Long.parseLong(req.getParameter("cid"));
        
        commentSupportService.remove(QueryWrapper.create()
                .where(COMMENT_SUPPORT_TABLE.CID.eq(cid)));
        
        VideoCommentTable data = videoCommentService.getById(cid);
        if(data != null) {
            videoCommentService.removeById(data.getId());
            String videoCommentCountKey = RedisKey.videoCommentCountKey(data.getVid());
            if(redisUtils.exists(videoCommentCountKey)){
                Integer value = redisUtils.getValue(videoCommentCountKey, Integer.class);
                redisUtils.setValue(videoCommentCountKey,value-1,114514);
            }
        }
        
        String supportkey = RedisKey.videoCommentSupportKey(cid);
        redisUtils.deleteValue(supportkey + cid);
        
        return BaseResult.success();
    }
    @PostMapping("/follow")
    @AuthRequired
    public BaseResult<None> followUser(HttpServletRequest req) {
        var uid = Auth.getToken().getId();
        var tid = Long.parseLong(req.getParameter("tid"));
        if(uid.equals(tid)){
            return BaseResult.fail("You can't follow yourself");
        }
        var action = req.getParameter("action").equals("1");
        UFavoriteMessage message = UFavoriteMessage.builder()
                .tid(tid)
                .uid(uid)
                .action(action).build();
        kafkaTemplate.send("user-favorite", JSONObject.toJSONString(message));
        return BaseResult.success();
    }
    
}
