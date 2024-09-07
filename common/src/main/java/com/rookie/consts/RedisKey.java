package com.rookie.consts;

public class RedisKey {
    public static String videoCollectionCountKey(Long videoId) {
        return "video:collection:count:" + videoId;
    }

    public static String videoCommentCountKey(Long videoId) {
        return "video:comment:count:" + videoId;
    }

    public static String videoFavoriteCountKey(Long videoId) {
        return "video:favorite:count:" + videoId;
    }

    public static String videoPlayCountKey(Long videoId) {
        return "video:play:count:" + videoId;
    }
}
