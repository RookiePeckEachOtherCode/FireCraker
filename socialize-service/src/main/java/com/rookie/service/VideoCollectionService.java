package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.consts.RedisKey;
import com.rookie.mapper.VideoCollectionMapper;
import com.rookie.model.Message;
import com.rookie.model.entity.VideoCollectionTable;
import com.rookie.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rookie.model.entity.table.VideoCollectionTableTableDef.VIDEO_COLLECTION_TABLE;

@Service
public class VideoCollectionService extends ServiceImpl<VideoCollectionMapper, VideoCollectionTable> implements IVideoCollectionService {
    @Resource
    private RedisUtils redisUtils;

    @KafkaListener(topics = "video-collection", groupId = "video-collection-group")
    public void videoCollection(String message) {
        var videoCollectionMessage = JSONObject.parseObject(message, Message.class);

        var key = RedisKey.videoCollectionCountKey(videoCollectionMessage.getVideoId());

        if (videoCollectionMessage.getAction()&&
                !exists(QueryWrapper.create()
                        .where(VIDEO_COLLECTION_TABLE.VID.eq(videoCollectionMessage.getVideoId()))
                        .and(VIDEO_COLLECTION_TABLE.UID.eq(videoCollectionMessage.getUserId()))
                )
        ) {
            var videoCollectionTable = VideoCollectionTable.builder()
                    .uid(videoCollectionMessage.getUserId())
                    .vid(videoCollectionMessage.getVideoId())
                    .createTime(System.currentTimeMillis())
                    .build();
            
                save(videoCollectionTable);
                if (redisUtils.exists(key)) {
                    Integer value = redisUtils.getValue(key, Integer.class);
                    redisUtils.setValue(key, value + 1, 114514);
                } else {
                    List<VideoCollectionTable> list = list(QueryWrapper.create()
                            .where(VIDEO_COLLECTION_TABLE.VID.eq(videoCollectionMessage.getVideoId())));
                    redisUtils.setValue(key, list.size(), 114514);
                }
            return;
        }

        var dbData = getOne(QueryWrapper.create()
                .where(VIDEO_COLLECTION_TABLE.UID.eq(videoCollectionMessage.getUserId()))
                .where(VIDEO_COLLECTION_TABLE.VID.eq(videoCollectionMessage.getVideoId())));
        if (dbData != null) {
            removeById(dbData.getId());
            Integer value = redisUtils.getValue(key, Integer.class);
            redisUtils.setValue(key, value - 1, 114514);
        }
    }
}
