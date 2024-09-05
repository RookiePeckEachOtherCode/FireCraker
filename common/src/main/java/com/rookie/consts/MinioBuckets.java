package com.rookie.consts;

public class MinioBuckets {
    final static public  String BUCKET_USER_AVATAR="user-avatar";
    final static public  String BUCKET_VIDEO="video";
    final static public  String BUCKET_VIDEO_COVER="video-cover";
    
    static public String GetFileCodeByUrl(String url){
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
    
}
