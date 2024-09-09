package com.rookie;


import com.rookie.config.FeignClientConfig;
import com.rookie.model.dto.UserInfoDTO;
import com.rookie.model.result.BaseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user-service", configuration = FeignClientConfig.class)
public interface UserServiceClient {
    @GetMapping("/info/{id}")
    BaseResult<UserInfoDTO> getUserInfo(@PathVariable String id);
}
