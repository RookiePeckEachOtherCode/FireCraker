package com.rookie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@AllArgsConstructor
@Getter
public class FileUploadDTO implements Serializable {
    private String fileUrl;

    public static FileUploadDTO fromUrl(String url) {
        return new FileUploadDTO(url);
    }
}
