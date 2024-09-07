package com.rookie.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileBuckets {
    VIDEO_BUCKET("video"),
    AVATAR_BUCKET("avatar"),
    COVER_BUCKET("cover");

    private final String bucketName;

    public static FileBuckets fromBucketName(String bucketName) {
        for (FileBuckets fileBucket : FileBuckets.values()) {
            if (fileBucket.getBucketName().equals(bucketName)) {
                return fileBucket;
            }
        }
        return null;
    }
}
