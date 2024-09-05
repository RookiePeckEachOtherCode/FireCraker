package com.rookie.utils;

import com.rookie.config.MinioConfigProp;
import com.rookie.consts.MinioBuckets;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.minio.MinioClient;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class MinioUtils {
    @Autowired
    private MinioClient minioClient;
    @Resource
    private MinioConfigProp minioConfigProp;
    
    public String UploadUserAvatar(MultipartFile file,String uid)throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return UploadImg(file, MinioBuckets.BUCKET_USER_AVATAR,uid);
    }
    public String UploadVideoCover(MultipartFile file,String vid)throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return UploadImg(file,MinioBuckets.BUCKET_VIDEO_COVER,vid);
    }

    public String UploadVideo(MultipartFile file,String vid)throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return UploadVideo(file,MinioBuckets.BUCKET_VIDEO,vid);
    }
    public Void DeleteVideo(String url)throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return DeleteObject(url,MinioBuckets.BUCKET_VIDEO);
    }

    private String UploadImg(MultipartFile file,String bucket,String id) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException  {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String fileCode = id + fileName.substring(fileName.lastIndexOf('.'));

        PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucket).object(fileCode)
                .stream(file.getInputStream(), file.getSize(), -1).contentType("image/png").contentType("image/jpeg").build();
        minioClient.putObject(objectArgs);
        
        return minioConfigProp.getImgHost() + "/" + bucket + "/" + fileCode;
        
    }
    private String UploadVideo(MultipartFile file, String bucket,String id)throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException{
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String fileCode = id+ fileName.substring(fileName.lastIndexOf('.'));
        PutObjectArgs objectArgs=PutObjectArgs.builder().bucket(bucket).object(fileCode)
                .stream(file.getInputStream(), file.getSize(), -1).contentType("video/mp4").contentType("video/mp4").build();
        minioClient.putObject(objectArgs);
        return minioConfigProp.getImgHost() + "/" + bucket + "/" + fileCode;
    }

    private Void DeleteObject(String url, String bucket) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String filecode=MinioBuckets.GetFileCodeByUrl(url);
        var args = RemoveObjectArgs.builder().bucket(bucket).object(filecode).build();
        minioClient.removeObject(args);
        return null;
    }
    
}
