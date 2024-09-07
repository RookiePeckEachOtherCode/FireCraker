package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.consts.RedisKey;
import com.rookie.mapper.VideoFavoriteMapper;
import com.rookie.model.Message;
import com.rookie.model.entity.VideoFavoriteTable;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.rookie.model.entity.table.VideoFavoriteTableTableDef.VIDEO_FAVORITE_TABLE;

@Service
public class VideoFavoriteService extends ServiceImpl<VideoFavoriteMapper, VideoFavoriteTable> implements IVideoFavoriteService {
    @Resource
    private RedisTemplate<String, Integer> redisTemplate;


    @KafkaListener(topics = "video-favorite", groupId = "video-favorite-group")
    public void videoFavorite(String message) {
        var videoFavoriteMessage = JSONObject.parseObject(message, Message.class);


        var key = RedisKey.videoFavoriteCountKey(videoFavoriteMessage.getVideoId());

        //TODO if key not exists, load from db and set to redis

        if (videoFavoriteMessage.getAction()) {
            var videoFavoriteTable = VideoFavoriteTable.builder()
                    .uid(videoFavoriteMessage.getUserId())
                    .vid(videoFavoriteMessage.getVideoId())
                    .createTime(System.currentTimeMillis())
                    .build();
            save(videoFavoriteTable);
            redisTemplate.opsForValue().increment(key, 1);
            return;
        }

        var dbData = getOne(QueryWrapper.create()
                .where(VIDEO_FAVORITE_TABLE.UID.eq(videoFavoriteMessage.getUserId()))
                .where(VIDEO_FAVORITE_TABLE.VID.eq(videoFavoriteMessage.getVideoId())));

        if (dbData != null) {
            removeById(dbData.getId());
            redisTemplate.opsForValue().decrement(key, 1);
        }
    }

}
