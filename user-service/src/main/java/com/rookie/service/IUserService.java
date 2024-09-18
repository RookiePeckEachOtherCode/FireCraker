package com.rookie.service;

import com.mybatisflex.core.service.IService;
import com.rookie.model.dto.UserSelfInfoDTO;
import com.rookie.model.entity.UserTable;

public interface IUserService extends IService<UserTable> {
     UserSelfInfoDTO UserFullInfo(Long uid);
}
