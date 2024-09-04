package com.rookie;

import com.rookie.model.result.BaseResult;
import com.rookie.model.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public BaseResult<String> exceptionHandler(Exception e) {
        log.error(e.getMessage());
        return BaseResult.of(ResultCode.FAIL, e.getMessage());
    }
}
