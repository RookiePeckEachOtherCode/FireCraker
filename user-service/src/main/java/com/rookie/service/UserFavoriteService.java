package com.rookie.service;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.mapper.UserFavoriteMapper;
import com.rookie.model.entity.UserFavoriteTable;
import org.springframework.stereotype.Service;

@Service
public class UserFavoriteService extends ServiceImpl<UserFavoriteMapper, UserFavoriteTable> implements IUserFavoriteService {
    
}
