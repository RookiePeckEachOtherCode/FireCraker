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
import com.rookie.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rookie.model.entity.table.VideoCommentTableTableDef.VIDEO_COMMENT_TABLE;

@Service
public class VideoCommentService extends ServiceImpl<VideoCommentMapper, VideoCommentTable> implements IVideoCommentService {
    @Resource
    RedisUtils redisUtils;

    @KafkaListener(topics = "video-comment", groupId = "video-comment-group")
    public void videoComment(String message) {
        var videoCommentMessage = JSONObject.parseObject(message, CommentMessage.class);
        var key = RedisKey.videoCommentCountKey(videoCommentMessage.getVid());
            var videoCommentTable = VideoCommentTable.builder()
                    .uid(videoCommentMessage.getUid())
                    .vid(videoCommentMessage.getVid())
                    .content(videoCommentMessage.getContent())
                    .createTime(System.currentTimeMillis())
                    .updateTime(System.currentTimeMillis())
                    .build();
            save(videoCommentTable);
            if(redisUtils.exists(key)){
                Integer value = redisUtils.getValue(key, Integer.class);
                redisUtils.setValue(key,value+1,114514);
            }else{
                List<VideoCommentTable> list = list(QueryWrapper.create()
                        .where(VIDEO_COMMENT_TABLE.VID.eq(videoCommentMessage.getVid())));
                redisUtils.setValue(key,list.size(),114514);
            }
    }
    

}

