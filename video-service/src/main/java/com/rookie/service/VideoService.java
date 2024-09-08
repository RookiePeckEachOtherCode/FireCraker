package com.rookie.service;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.mapper.VideoMapper;
import com.rookie.model.entity.VideoTable;
import org.springframework.stereotype.Service;

@Service
public class VideoService extends ServiceImpl<VideoMapper, VideoTable> implements IVideoService {
}
