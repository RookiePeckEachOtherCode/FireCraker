package com.rookie.service;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.mapper.*;
import com.rookie.model.dto.UserSelfInfoDTO;
import com.rookie.model.entity.UserTable;
import com.rookie.model.entity.table.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import static com.rookie.model.entity.table.UserFavoriteTableTableDef.USER_FAVORITE_TABLE;
import static com.rookie.model.entity.table.UserTableTableDef.USER_TABLE;
import static com.rookie.model.entity.table.UserVideoCollectionTableTableDef.USER_VIDEO_COLLECTION_TABLE;
import static com.rookie.model.entity.table.VideoFavoriteTableTableDef.VIDEO_FAVORITE_TABLE;
import static com.rookie.model.entity.table.VideoTableTableDef.VIDEO_TABLE;

@Service
public class UserService extends ServiceImpl<UserMapper, UserTable> implements IUserService {
    @Resource
    private VideoMapper videoMapper;
    @Resource
    private VideoFavMapper videoFavoriteMapper;
    @Resource
    private UserFavoriteMapper userFavoriteMapper;
    @Resource
    private UserCollectionMapper userCollectionMapper;


    @Override
    public UserSelfInfoDTO UserFullInfo(Long uid) {
        UserTable dbUser = getOneByEntityId(UserTable.ID(uid));
        long up_cnt = QueryChain.of(videoMapper).where(VIDEO_TABLE.UID.eq(uid)).count();
        long fav_cnt = QueryChain.of(videoFavoriteMapper).where(VIDEO_FAVORITE_TABLE.UID.eq(uid)).count();
        long fal_cnt = QueryChain.of(userFavoriteMapper).where(USER_FAVORITE_TABLE.UID.eq(uid)).count();
        long fed_cnt = QueryChain.of(userFavoriteMapper).where(USER_FAVORITE_TABLE.FAV_UID.eq(uid)).count();
        long col_cnt = QueryChain.of(userCollectionMapper).where(USER_VIDEO_COLLECTION_TABLE.UID.eq(uid)).count();
        return UserSelfInfoDTO.builder()
                .id(dbUser.getId())
                .name(dbUser.getName())
                .avatar(dbUser.getAvatar())
                .signature(dbUser.getSignature())
                .favCnt(fav_cnt) 
                .colCnt(col_cnt) 
                .updCnt(up_cnt) 
                .falCnt(fal_cnt)
                .fedCnt(fed_cnt)
                .showCollection(dbUser.isShowCollection())
                .build();
    }
}
