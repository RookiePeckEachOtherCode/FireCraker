package com.rookie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class FileUploadDTO {
    private String fileUrl;

    public static FileUploadDTO fromUrl(String url) {
        return new FileUploadDTO(url);
    }
}
