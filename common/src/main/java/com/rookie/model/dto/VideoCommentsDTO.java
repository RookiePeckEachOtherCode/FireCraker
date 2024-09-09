package com.rookie.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class VideoCommentsDTO {
    List<CommentInfo> clist;
}
