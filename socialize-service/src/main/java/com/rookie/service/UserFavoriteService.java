package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.mapper.UserFavoriteMapper;
import com.rookie.model.Message;
import com.rookie.model.UFavoriteMessage;
import com.rookie.model.entity.UserFavoriteTable;
import com.rookie.model.entity.table.UserFavoriteTableTableDef;
import com.rookie.model.entity.table.UserVideoCollectionTableTableDef;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.rookie.model.entity.table.UserFavoriteTableTableDef.USER_FAVORITE_TABLE;
import static com.rookie.model.entity.table.UserVideoCollectionTableTableDef.USER_VIDEO_COLLECTION_TABLE;

@Service
public class UserFavoriteService extends ServiceImpl<UserFavoriteMapper, UserFavoriteTable> implements IUserFavoriteService {

    @KafkaListener(topics = "user-favorite", groupId = "user-favorite-group")
    public void UserFavorite(String message) {
        var FavMessage = JSONObject.parseObject(message, UFavoriteMessage.class);
        if(FavMessage.getAction()){
            UserFavoriteTable build = UserFavoriteTable.builder()
                    .uid(FavMessage.getUid())
                    .favUid(FavMessage.getTid())
                    .createTime(System.currentTimeMillis())
                    .build();
            save(build);
        }else{
            remove(QueryWrapper.create()
                    .where(USER_FAVORITE_TABLE.UID.eq(FavMessage.getUid()))
                    .and(USER_FAVORITE_TABLE.FAV_UID.eq(FavMessage.getTid())));       
        }



    }
}
