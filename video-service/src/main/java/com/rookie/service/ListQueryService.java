package com.rookie.service;

import com.mybatisflex.core.query.QueryChain;
import com.rookie.UserServiceClient;
import com.rookie.consts.RedisKey;
import com.rookie.mapper.*;
import com.rookie.model.dto.CommentInfo;
import com.rookie.model.dto.VideoSimpleInfo;
import com.rookie.model.entity.UserVideoHistoryTable;
import com.rookie.model.entity.VideoCollectionTable;
import com.rookie.model.entity.VideoFavoriteTable;
import com.rookie.model.entity.VideoTable;
import com.rookie.utils.DataBuilder;
import com.rookie.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rookie.model.entity.table.UserVideoHistoryTableTableDef.USER_VIDEO_HISTORY_TABLE;
import static com.rookie.model.entity.table.VideoCommentTableTableDef.VIDEO_COMMENT_TABLE;
import static com.rookie.model.entity.table.VideoFavoriteTableTableDef.VIDEO_FAVORITE_TABLE;
import static com.rookie.model.entity.table.VideoTableTableDef.VIDEO_TABLE;

@Service
public class ListQueryService implements IListQueryService {
    @Resource
    private UserFavoriteVideoMapper userFavoriteVideoMapper;
    @Resource
    private VideoMapper videoMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserCollectionMapper userCollectionMapper;
    @Resource
    private VideoHistoryMapper videoHistoryMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserServiceClient userServiceClient;


    @Override
    public List<CommentInfo> GetVideoComment(String vid, Integer offset, Integer size) {
        var videoComments = QueryChain.of(commentMapper)
                .where(VIDEO_COMMENT_TABLE.VID.eq(Long.valueOf(vid)))
                .offset(offset)
                .limit(size)
                .list();

        return videoComments.stream().map(it -> {
            var userRes = userServiceClient.getUserInfo(it.getId().toString());
            if (!userRes.ok()) {
                return null;
            }

            var user = userRes.getData();

            var favCnt = redisUtils.getValue(RedisKey.videoCommentSupportKey(it.getId()), Integer.class);
            var isFav = redisUtils.getValue(RedisKey.videoCommentIsSupportKey(user.getId(), it.getId()), Integer.class);

            return CommentInfo.builder()
                    .id(it.getId())
                    .uid(it.getUid())
                    .content(it.getContent())
                    .fav_cnt(DataBuilder.buildCount(favCnt))
                    .is_fav(isFav == 1)
                    .name(user.getName())
                    .avatar_url(user.getAvatar())
                    .build();
        }).toList();
    }

    @Override
    public List<VideoSimpleInfo> GetUserFavVideo(String uid, Integer offset, Integer size) {
        var videoIds = QueryChain.of(userFavoriteVideoMapper)
                .select(VIDEO_FAVORITE_TABLE.VID)
                .where(VIDEO_FAVORITE_TABLE.UID.eq(uid))
                .offset(offset)
                .limit(size)
                .list()
                .stream()
                .map(VideoFavoriteTable::getVid)
                .toList();


        return VideoIdsToVideoSimpleInfo(videoIds);
    }

    @Override
    public List<VideoSimpleInfo> GetUserColVideo(String uid, Integer offset, Integer size) {
        var videoIds = QueryChain.of(userCollectionMapper)
                .select(VIDEO_COMMENT_TABLE.VID)
                .where(VIDEO_COMMENT_TABLE.UID.eq(uid))
                .offset(offset)
                .limit(size)
                .list()
                .stream()
                .map(VideoCollectionTable::getVid)
                .toList();

        return VideoIdsToVideoSimpleInfo(videoIds);
    }

    @Override
    public List<VideoSimpleInfo> GetUserHistory(String uid, Integer offset, Integer size) {
        var videoIds = QueryChain.of(videoHistoryMapper)
                .select(USER_VIDEO_HISTORY_TABLE.VID)
                .where(USER_VIDEO_HISTORY_TABLE.UID.eq(uid))
                .offset(offset)
                .limit(size)
                .list()
                .stream()
                .map(UserVideoHistoryTable::getVid)
                .toList();

        return VideoIdsToVideoSimpleInfo(videoIds);
    }

    @Override
    public List<VideoSimpleInfo> SearchVideo(String keyword, Integer offset, Integer size) {
        var videoIds = QueryChain.of(videoMapper)
                .select(VIDEO_TABLE.ID)
                .where(VIDEO_TABLE.TITLE.like("%" + keyword + "%"))
                .offset(offset)
                .limit(size)
                .list()
                .stream()
                .map(VideoTable::getId)
                .toList();

        return VideoIdsToVideoSimpleInfo(videoIds);
    }

    @Override
    public List<VideoSimpleInfo> GetUserPublishedVideo(String uid, Integer offset, Integer size) {
        var videoIds = QueryChain.of(videoMapper)
                .select(VIDEO_TABLE.ID)
                .where(VIDEO_TABLE.UID.eq(Long.valueOf(uid)))
                .offset(offset)
                .limit(size)
                .list()
                .stream()
                .map(VideoTable::getId)
                .toList();

        return VideoIdsToVideoSimpleInfo(videoIds);
    }

    @NotNull
    private List<VideoSimpleInfo> VideoIdsToVideoSimpleInfo(List<Long> videoIds) {
        var videos = videoIds.stream().map(it ->
                QueryChain.of(videoMapper)
                        .where(VIDEO_TABLE.ID.eq(it))
                        .one()
        ).toList();

        return videos.stream().map(it -> {
            var facCnt = redisUtils.getValue(RedisKey.videoFavoriteCountKey(it.getId()), Integer.class);
            var comCnt = redisUtils.getValue(RedisKey.videoCommentCountKey(it.getId()), Integer.class);
            var playCnt = redisUtils.getValue(RedisKey.videoPlayCountKey(it.getId()), Integer.class);

            return VideoSimpleInfo.builder()
                    .id(it.getId())
                    .title(it.getTitle())
                    .video_url(it.getVideoUrl())
                    .fav_cnt(DataBuilder.buildCount(facCnt))
                    .com_cnt(DataBuilder.buildCount(comCnt))
                    .play_cnt(DataBuilder.buildCount(playCnt))
                    .build();
        }).toList();
    }

}
