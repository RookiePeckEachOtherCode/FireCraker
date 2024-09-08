package com.rookie.utils;

import com.rookie.consts.ComposeConst;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ComposeUtils {
    public static void compressAndUploadVideo(MultipartFile originalVideoFile, String filename, MinioClient minioClient, String bucketName) throws IOException, InterruptedException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 创建临时文件
        File tempFile = File.createTempFile("temp_", filename);
        originalVideoFile.transferTo(tempFile);
        File compressedFile = new File("compressed_" + tempFile.getName());

        try {
            // 使用 ffmpeg 压缩视频
            String[] cmd = new String[]{
                    "ffmpeg", "-i", tempFile.getAbsolutePath(),
                    "-vcodec", "libx265", "-crf", ComposeConst.CRF,
                    compressedFile.getAbsolutePath()
            };
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Process process = processBuilder.start();
            process.waitFor();

            // 检查压缩文件是否存在
            if (compressedFile.exists()) {
                // 上传到 MinIO
                minioClient.putObject(
                        PutObjectArgs.builder().bucket(bucketName)
                                .object(filename)
                                .stream(Files.newInputStream(compressedFile.toPath()), compressedFile.length(), -1)
                                .contentType("video/mp4")
                                .build()
                );
                System.out.println("Video uploaded successfully.");
            } else {
                throw new RuntimeException("Video compression failed.");
            }
        } finally {
            // 清理临时文件
            tempFile.delete();
            compressedFile.delete();
        }
    }

}
