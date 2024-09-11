package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.consts.RedisKey;
import com.rookie.mapper.CommentSupportMapper;
import com.rookie.model.CSupportMessage;
import com.rookie.model.entity.CommentSupportTable;
import com.rookie.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rookie.model.entity.table.CommentSupportTableTableDef.COMMENT_SUPPORT_TABLE;

@Service
public class CommentSupportService extends ServiceImpl<CommentSupportMapper, CommentSupportTable> implements ICommentSupportService {

    @Resource
    RedisUtils redisUtils;

    @KafkaListener(topics = "comment-support", groupId = "video-support-group")
    public void commentSupport(String message) {
        var mes = JSONObject.parseObject(message, CSupportMessage.class);
        var uid = mes.getUid();
        var cid = mes.getCid();
        var action = mes.getAction();
        var table = CommentSupportTable.builder()
                .cid(cid)
                .uid(uid)
                .createTime(System.currentTimeMillis());
        save(table.build());
        String key = RedisKey.videoCommentSupportKey(cid);
        String isFavKey = RedisKey.videoCommentIsSupportKey(uid, cid);
        if (action) {
            if (redisUtils.exists(key)) {
                Integer value = redisUtils.getValue(key, Integer.class);
                redisUtils.setValue(key, value + 1);
            } else {
                try {
                    List<CommentSupportTable> list = list(QueryWrapper.create().where(COMMENT_SUPPORT_TABLE.CID.eq(cid)));
                    redisUtils.setValue(key, list.size());
                } catch (MyBatisSystemException ignored) {
                    redisUtils.setValue(key, 1);
                }
            }

            if (redisUtils.exists(isFavKey)) {
                redisUtils.setValue(isFavKey, 1);
            } else {
                var isFav = getOne(QueryWrapper.create().where(COMMENT_SUPPORT_TABLE.CID.eq(cid)).where(COMMENT_SUPPORT_TABLE.UID.eq(uid))) != null;
                redisUtils.setValue(isFavKey, isFav ? 1 : 0);
            }
        } else {
            remove(QueryWrapper.create()
                    .where(COMMENT_SUPPORT_TABLE.UID.eq(uid)
                            .and(COMMENT_SUPPORT_TABLE.CID.eq(cid))));
            if (redisUtils.exists(key)) {
                Integer value = redisUtils.getValue(key, Integer.class);
                redisUtils.setValue(key, value - 1);
            }

            if (redisUtils.exists(isFavKey)) {
                redisUtils.setValue(isFavKey, 0);
            }
        }
    }
}
