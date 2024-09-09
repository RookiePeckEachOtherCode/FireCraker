package com.rookie.consts;

public class RedisKey {
    public static String videoCollectionCountKey(Long videoId) {
        return "video-collection-" + videoId;
    }

    public static String videoCommentCountKey(Long videoId) {
        return "video-comment-" + videoId;
    }

    public static String videoFavoriteCountKey(Long videoId) {
        return "video-favorite-" + videoId;
    }

    public static String videoPlayCountKey(Long videoId) {
        return "video-play-" + videoId;
    }

    public static String videoCommentSupportKey(Long cid) {
        return "comment-support-" + cid;
    }
    
    public static String UserFavVideoKey(Long userId,Long videoId) {return "video-fav-v:" + videoId+"u:"+userId;}
    
    public static String UserColVideoKey(Long userId,Long videoId) {return "video-col-v:" + videoId+"u:"+userId;}
}
