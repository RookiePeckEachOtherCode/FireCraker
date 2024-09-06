package com.rookie.controller;

import com.rookie.annotation.AuthRequired;
import com.rookie.config.MinioConfigProp;
import com.rookie.model.FileBuckets;
import com.rookie.model.dto.FileUploadDTO;
import com.rookie.model.result.BaseResult;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
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
                minioConfigProp.getImgHost() + "/" + FileBuckets.VIDEO_BUCKET.getBucketName() + "/" + filename
        ));
    }

    @PostMapping("/upload/image")
   /* @AuthRequired*/
    public BaseResult<FileUploadDTO> uploadImage(
            @RequestPart("image") MultipartFile imageFile,
            @RequestParam("filename") String filename,
            @RequestParam("type") String type
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
                        .stream(inputStream, -1, 5242880) //5mb
                        .build()
        );

        return BaseResult.success(FileUploadDTO.fromUrl(
                minioConfigProp.getImgHost() + "/" + FileBuckets.VIDEO_BUCKET + "/" + filename
        ));
    }
}
