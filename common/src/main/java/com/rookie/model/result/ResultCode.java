package com.rookie.model.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "success"),
    FAIL(500, "fail"),
    UNAUTHORIZED(401, "unauthorized"),
    NOT_FOUND(404, "not found"),
    UNKNOWN_ERROR(500, "unknown error"),
    FILE_UPLOAD_ERROR(500, "file upload error");

    private final Integer code;
    private final String msg;
}
