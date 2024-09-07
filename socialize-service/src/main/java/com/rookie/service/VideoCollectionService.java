package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.consts.RedisKey;
import com.rookie.mapper.VideoCollectionMapper;
import com.rookie.model.Message;
import com.rookie.model.entity.VideoCollectionTable;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.rookie.model.entity.table.VideoCollectionTableTableDef.VIDEO_COLLECTION_TABLE;

@Service
public class VideoCollectionService extends ServiceImpl<VideoCollectionMapper, VideoCollectionTable> implements IVideoCollectionService {
    @Resource
    private RedisTemplate<String, Integer> redisTemplate;

    @KafkaListener(topics = "video-collection", groupId = "video-collection-group")
    public void videoCollection(String message) {
        var videoCollectionMessage = JSONObject.parseObject(message, Message.class);

        var key = RedisKey.videoCollectionCountKey(videoCollectionMessage.getVideoId());

        //TODO if key not exists, load from db and set to redis

        if (videoCollectionMessage.getAction()) {
            var videoCollectionTable = VideoCollectionTable.builder()
                    .uid(videoCollectionMessage.getUserId())
                    .vid(videoCollectionMessage.getVideoId())
                    .createTime(System.currentTimeMillis())
                    .build();
            save(videoCollectionTable);
            redisTemplate.opsForValue().increment(key, 1);
            return;
        }

        var dbData = getOne(QueryWrapper.create()
                .where(VIDEO_COLLECTION_TABLE.UID.eq(videoCollectionMessage.getUserId()))
                .where(VIDEO_COLLECTION_TABLE.VID.eq(videoCollectionMessage.getVideoId())));

        if (dbData != null) {
            removeById(dbData.getId());
            redisTemplate.opsForValue().decrement(key, 1);
        }
    }
}
