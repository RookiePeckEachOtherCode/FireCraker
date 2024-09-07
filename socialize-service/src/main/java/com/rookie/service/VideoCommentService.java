package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.consts.RedisKey;
import com.rookie.mapper.VideoCommentMapper;
import com.rookie.model.CSupportMessage;
import com.rookie.model.CommentMessage;
import com.rookie.model.Message;
import com.rookie.model.entity.VideoCommentTable;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.rookie.model.entity.table.VideoCommentTableTableDef.VIDEO_COMMENT_TABLE;

@Service
public class VideoCommentService extends ServiceImpl<VideoCommentMapper, VideoCommentTable> implements IVideoCommentService {
    @Resource
    private RedisTemplate<String, Integer> redisTemplate;

    @KafkaListener(topics = "video-comment", groupId = "video-comment-group")
    public void videoComment(String message) {
        System.out.println(message);
//        var videoCommentMessage = JSONObject.parseObject(message, CommentMessage.class);
//        var key = RedisKey.videoCommentCountKey(videoCommentMessage.getVid());
//        //TODO if key not exists, load from db and set to redis
//            var videoCommentTable = VideoCommentTable.builder()
//                    .uid(videoCommentMessage.getUid())
//                    .vid(videoCommentMessage.getVid())
//                    .content(videoCommentMessage.getContent())
//                    .createTime(System.currentTimeMillis())
//                    .updateTime(System.currentTimeMillis())
//                    .build();
//            save(videoCommentTable);
//            redisTemplate.opsForValue().increment(key, 1);
    }
    

}

