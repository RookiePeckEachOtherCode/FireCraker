package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.mapper.CommentSupportMapper;
import com.rookie.model.CSupportMessage;
import com.rookie.model.entity.CommentSupportTable;
import com.rookie.model.entity.table.CommentSupportTableTableDef;
import com.rookie.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.rookie.model.entity.table.CommentSupportTableTableDef.COMMENT_SUPPORT_TABLE;

@Service
public class CommentSupportService extends ServiceImpl<CommentSupportMapper, CommentSupportTable> implements ICommentSupportService {
    
    @Resource
    RedisUtils redisUtils;
    
    @KafkaListener(topics = "comment-support",groupId = "video-comment-group")
    public void commentSupport(String message) {
        var mes = JSONObject.parseObject(message, CSupportMessage.class);
        var uid=mes.getUid();
        var cid=mes.getCid();
        var action=mes.getAction();
        var table= CommentSupportTable.builder()
                .cid(cid)
                .uid(uid)
                .createTime(System.currentTimeMillis());
        save(table.build());
        String key="comment-support-"+cid;
        if(action){
            if(redisUtils.exists(key)){
                Integer i = redisUtils.getValue(key, Integer.class);
                redisUtils.setValue(key,i,72);
            }else{
                redisUtils.setValue(key,1,72);
            }
        }else{
            remove(QueryWrapper.create()
                    .where(COMMENT_SUPPORT_TABLE.UID.eq(uid)
                            .and(COMMENT_SUPPORT_TABLE.CID.eq(cid))));
            if(redisUtils.exists(key)){
                Integer i = redisUtils.getValue(key, Integer.class);
                redisUtils.setValue(key,i-1,72);
            }
        }

        
    }
    
}
