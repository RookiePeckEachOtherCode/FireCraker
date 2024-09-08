package com.rookie.controller;

import com.rookie.annotation.AuthRequired;
import com.rookie.config.MinioConfigProp;
import com.rookie.model.FileBuckets;
import com.rookie.model.dto.FileUploadDTO;
import com.rookie.model.result.BaseResult;
import com.rookie.utils.ComposeUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
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
    @Resource
    private MinioConfigProp minioConfigProp;

    @PostMapping("/upload/video")
    @AuthRequired
    public BaseResult<FileUploadDTO> uploadVideo(
            @RequestParam("video") MultipartFile videoFile,
            @RequestParam("filename") String filename,
            @RequestParam("token") String token
    ) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InterruptedException {
        var bucket = FileBuckets.VIDEO_BUCKET;

        //TODO Compress video and upload by kafka
        ComposeUtils.compressAndUploadVideo(videoFile, filename, minioClient, bucket.getBucketName());

        return BaseResult.success(FileUploadDTO.fromUrl(
                minioConfigProp.getImgHost() + "/" + FileBuckets.VIDEO_BUCKET.getBucketName() + "/" + filename
        ));
    }

    @PostMapping("/upload/image")
    @AuthRequired
    public BaseResult<FileUploadDTO> uploadImage(
            @RequestPart("image") MultipartFile imageFile,
            @RequestParam("filename") String filename,
            @RequestParam("type") String type,
            @RequestParam("token") String token
    ) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        var bucket = FileBuckets.fromBucketName(type);
        if (bucket == null) {
            return BaseResult.fail("Invalid bucket type");
        }

        InputStream inputStream = imageFile.getInputStream();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket.getBucketName())
                        .object(filename)
                        .stream(inputStream, imageFile.getSize(), -1)
                        .build()
        );

        return BaseResult.success(FileUploadDTO.fromUrl(
                minioConfigProp.getImgHost() + "/" + bucket.getBucketName() + "/" + filename
        ));
    }
}
