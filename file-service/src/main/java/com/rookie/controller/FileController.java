package com.rookie.controller;

import com.rookie.annotation.AuthRequired;
import com.rookie.model.FileBuckets;
import com.rookie.model.dto.FileUploadDTO;
import com.rookie.model.result.BaseResult;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private MinioClient minioClient;

    @PostMapping("/upload/video")
    @AuthRequired
    public BaseResult<FileUploadDTO> uploadVideo(@RequestParam("video") MultipartFile videoFile, HttpServletRequest req) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        val filename = req.getParameter("filename");
        var bucket = FileBuckets.VIDEO_BUCKET;

        InputStream inputStream = videoFile.getInputStream();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket.getBucketName())
                        .object(filename)
                        .stream(inputStream, -1, 10485760)// 10MB
                        .build()
        );
        return BaseResult.success(FileUploadDTO.fromUrl(
                FileBuckets.VIDEO_BUCKET.getBucketName() + "/" + filename
        ));
    }

    @PostMapping("/upload/image")
    @AuthRequired
    public BaseResult<FileUploadDTO> uploadImage(@RequestParam("image") MultipartFile imageFile, HttpServletRequest req) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        val filename = req.getParameter("filename");
        var bucket = FileBuckets.fromBucketName(req.getParameter("type"));
        if (bucket == null) {
            return BaseResult.fail("Invalid bucket type");
        }

        InputStream inputStream = imageFile.getInputStream();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket.getBucketName())
                        .object(filename)
                        .stream(inputStream, -1, 5242880) //5mb
                        .build()
        );

        return BaseResult.success(FileUploadDTO.fromUrl(
                FileBuckets.VIDEO_BUCKET.getBucketName() + "/" + filename
        ));
    }
}
