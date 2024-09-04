package com.rookie.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseResult<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public static <T> BaseResult<T> of(ResultCode resultCode, T data) {
        return new BaseResult<>(resultCode.getCode(), resultCode.getMsg(), data);
    }

    public static <T> BaseResult<T> of(ResultCode resultCode) {
        return new BaseResult<>(resultCode.getCode(), resultCode.getMsg(), null);
    }
}



