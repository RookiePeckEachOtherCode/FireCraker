package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.consts.RedisKey;
import com.rookie.mapper.VideoFavoriteMapper;
import com.rookie.model.Message;
import com.rookie.model.entity.VideoFavoriteTable;
import com.rookie.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rookie.model.entity.table.VideoFavoriteTableTableDef.VIDEO_FAVORITE_TABLE;

@Service
public class VideoFavoriteService extends ServiceImpl<VideoFavoriteMapper, VideoFavoriteTable> implements IVideoFavoriteService {
    @Resource
    RedisUtils redisUtils;


    @KafkaListener(topics = "video-favorite", groupId = "video-favorite-group")
    public void videoFavorite(String message) {
        var videoFavoriteMessage = JSONObject.parseObject(message, Message.class);


        var key = RedisKey.videoFavoriteCountKey(videoFavoriteMessage.getVideoId());
        if (videoFavoriteMessage.getAction()) {
            var videoFavoriteTable = VideoFavoriteTable.builder()
                    .uid(videoFavoriteMessage.getUserId())
                    .vid(videoFavoriteMessage.getVideoId())
                    .createTime(System.currentTimeMillis())
                    .build();
            save(videoFavoriteTable);
            if(redisUtils.exists(key)){
                Integer value = redisUtils.getValue(key, Integer.class);
                redisUtils.setValue(key,value+1,114514);
            }else{
                List<VideoFavoriteTable> list = list(QueryWrapper.create().where(VIDEO_FAVORITE_TABLE.VID.eq(videoFavoriteMessage.getVideoId())));
                redisUtils.setValue(key,list.size()+1,114514);
            }
            return;
        }

        var dbData = getOne(QueryWrapper.create()
                .where(VIDEO_FAVORITE_TABLE.UID.eq(videoFavoriteMessage.getUserId()))
                .where(VIDEO_FAVORITE_TABLE.VID.eq(videoFavoriteMessage.getVideoId())));

        if (dbData != null) {
            removeById(dbData.getId());
            Integer value = redisUtils.getValue(key, Integer.class);
            redisUtils.setValue(key,value-1,114514);
        }
    }

}
