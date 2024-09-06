package com.rookie.service;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rookie.mapper.UserMapper;
import com.rookie.model.entity.UserTable;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, UserTable> implements IUserService {
    
    
}
