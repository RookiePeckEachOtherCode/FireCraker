package com.rookie.service;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryChain;
import com.rookie.consts.RedisKey;
import com.rookie.mapper.CommentMapper;
import com.rookie.mapper.UserMapper;
import com.rookie.mapper.VideoHistoryMapper;
import com.rookie.mapper.VideoMapper;
import com.rookie.model.HistoryMessage;
import com.rookie.model.dto.VideoFullInfo;
import com.rookie.model.dto.VideoSimpleInfo;
import com.rookie.model.entity.UserVideoHistoryTable;
import com.rookie.model.entity.VideoTable;
import com.rookie.model.entity.table.UserVideoHistoryTableTableDef;
import com.rookie.utils.DataBuilder;
import com.rookie.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.rookie.model.entity.table.UserVideoHistoryTableTableDef.USER_VIDEO_HISTORY_TABLE;
import static com.rookie.model.entity.table.VideoTableTableDef.VIDEO_TABLE;

@Service
public class VideoMainService implements IVideoMainService {
    @Resource
    KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    VideoHistoryMapper videoHistoryMapper;
    @Resource
    VideoMapper videoMapper;
    @Resource
    CommentMapper commentMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    RedisUtils redisUtils;

    @Override
    public List<VideoSimpleInfo> GetRecommendVideo(String uid, Integer offset, Integer size) {
        List<Long> videoIds;
        if(uid!=null){
            List<UserVideoHistoryTable> list = QueryChain.of(videoHistoryMapper)
                    .where(USER_VIDEO_HISTORY_TABLE.UID.eq(Long.parseLong(uid))).list();

            List<Long> read=new ArrayList<>();
            list.forEach(userVideoHistoryTable -> {read.add(userVideoHistoryTable.getVid());});
            videoIds= QueryChain.of(videoMapper)
                    .select(VIDEO_TABLE.ID)
                    .where(VIDEO_TABLE.ID.notIn(read))
                    .offset(offset)
                    .limit(size)
                    .list()
                    .stream()
                    .map(VideoTable::getId)
                    .toList();
        }else{
            videoIds= QueryChain.of(videoMapper)
                    .select(VIDEO_TABLE.ID)
                    .offset(offset)
                    .limit(size)
                    .list()
                    .stream()
                    .map(VideoTable::getId)
                    .toList();
        }


        return  VideoIdsToVideoSimpleInfo(videoIds);
    }

    @Override
    public VideoFullInfo GetVideoInfo(String vid, String uid) {
        String playkey = RedisKey.videoPlayCountKey(Long.valueOf(vid));
        if (redisUtils.exists(vid)) {
            Integer value = redisUtils.getValue(playkey, Integer.class);
            redisUtils.setValue(playkey, value + 1);
        } else {
            redisUtils.setValue(playkey, 1);
        }
        if (uid != null && !uid.isEmpty()) {
            HistoryMessage message = HistoryMessage.builder().vid(vid).uid(uid).build();
            kafkaTemplate.send("video-history", JSONObject.toJSONString(message));
        }

        Integer play_cnt = GetRedisData(playkey);
        Integer col_cnt = GetRedisData(RedisKey.videoCollectionCountKey(Long.valueOf(vid)));
        Integer fav_cnt = GetRedisData(RedisKey.videoFavoriteCountKey(Long.valueOf(vid)));
        boolean is_fav = false;
        boolean is_col = false;
        if (uid != null && !uid.isEmpty()) {
            is_fav = redisUtils.exists(RedisKey.UserFavVideoKey(Long.parseLong(uid), Long.parseLong(vid)));
            is_col = redisUtils.exists(RedisKey.UserColVideoKey(Long.parseLong(uid), Long.parseLong(vid)));
        }
        VideoTable data = QueryChain.of(videoMapper).where(VIDEO_TABLE.ID.eq(Long.parseLong(vid))).one();
        VideoFullInfo.VideoFullInfoBuilder info = VideoFullInfo.builder()
                .id(String.valueOf(data.getId()))
                .title(data.getTitle())
                .description(data.getDescription())
                .video_url(data.getVideoUrl())
                .cover_url(data.getCoverUrl())
                .update_time(data.getUpdateTime())
                .play_cnt(play_cnt)
                .fav_cnt(fav_cnt)
                .col_cnt(col_cnt)
                .is_fav(is_fav)
                .is_col(is_col)
                .uid(String.valueOf(data.getUid()))
                ;

        return info.build();
    }

    @KafkaListener(topics = "video-history", groupId = "video-history-group")
    public void VideoHistory(String message) {
        HistoryMessage historyMessage = JSONObject.parseObject(message, HistoryMessage.class);
        UserVideoHistoryTable build = UserVideoHistoryTable.builder()
                .vid(Long.valueOf(historyMessage.getVid()))
                .uid(Long.valueOf(historyMessage.getUid()))
                .createTime(System.currentTimeMillis())
                .build();
        List<UserVideoHistoryTable> list = QueryChain.of(videoHistoryMapper)
                .where(USER_VIDEO_HISTORY_TABLE.VID.eq(Long.parseLong(historyMessage.getVid()))
                ).and(USER_VIDEO_HISTORY_TABLE.UID.eq(Long.parseLong(historyMessage.getUid()))).list();
        
        if(list.isEmpty())videoHistoryMapper.insert(build);
        
    }

    @NotNull
    private Integer GetRedisData(String key) {
        if (redisUtils.exists(key)) {
            return redisUtils.getValue(key, Integer.class);
        } else {
            redisUtils.setValue(key, 1, 114514);
            return 1;
        }
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
