package com.rookie.service;

import com.rookie.model.dto.FileUploadDTO;
import com.rookie.model.result.BaseResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("file-service")
public interface FileServiceClient {
    @PostMapping(value = "/file/upload/image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BaseResult<FileUploadDTO> uploadImage(
            @RequestPart("image") MultipartFile imageFile,
            @RequestParam("filename") String filename,
            @RequestParam("type") String type
    );
    
}
