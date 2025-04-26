package com.highfive.meetu.infra.s3;

import com.highfive.meetu.global.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Duration DEFAULT_EXPIRATION = Duration.ofHours(1); // 1시간으로 고정

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;


    /**
     * S3에 저장된 이미지 Key를 기반으로 Presigned URL 생성
     *
     * @param key : 이미지가 저장된 S3 Key (예: "profile/uuid_파일명.jpg")
     *            → DB에서 가져온 profileImageKey, logoKey 등
     * @return    : 1시간 동안 유효한 접근 가능한 Presigned URL (null 가능성 있음)
     */
    public String getImageUrl(String key) {
        if (key == null || key.isBlank()) return null;
        return generatePresignedUrl(key, DEFAULT_EXPIRATION); // 1시간 유효 Presigned URL
    }


    /**
     * S3에 파일 업로드 (private 업로드)
     * @param file MultipartFile
     * @param dir 저장 디렉토리 (예: profile, resume 등)
     * @return S3 객체 key
     */
    public String uploadFile(MultipartFile file, String dir) {
        String filename = dir + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return filename;
        } catch (IOException e) {
            throw new BadRequestException("S3 업로드 실패: " + e.getMessage());
        }
    }


    /**
     * Presigned URL 생성 (1시간 고정)
     * @param filename S3 객체 key
     * @return Presigned URL
     */
    public String generatePresignedUrl(String filename) {
        return generatePresignedUrl(filename, DEFAULT_EXPIRATION);
    }


    /**
     * 업로드 + Presigned URL 한 번에 처리 (단순 미리보기용 등)
     * @param file MultipartFile
     * @param dir S3 디렉토리
     * @return Presigned URL (1시간 유효)
     */
    public String uploadFileAndGetUrl(MultipartFile file, String dir) {
        String key = uploadFile(file, dir);
        return generatePresignedUrl(key);
    }


    /**
     * Presigned URL 생성 (내부용)
     * @param filename S3 객체 key
     * @param duration 유효 시간
     * @return Presigned URL
     */
    private String generatePresignedUrl(String filename, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(duration)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
}

