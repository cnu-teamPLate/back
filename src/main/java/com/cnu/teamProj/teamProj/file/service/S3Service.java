package com.cnu.teamProj.teamProj.file.service;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {
    private final S3AsyncClient amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${spring.cloud.aws.region.static}")
    private String regionStr;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public S3Service(S3AsyncClient amazonS3) {
        this.amazonS3 = amazonS3;
    }

    /**
     * S3에 파일 업로드해주는 메소드
     * @param file - multipart/form-data 방식으로 파일에 대한 요청값을 받음
     * @param projId - 어떤 프로젝트와 연관된 파일인지 저장하기 위한 프로젝트 아이디
     * @return 파일을 업로드한 후 url 값을 반환함
     * */
    public String uploadFile(MultipartFile file, String projId) throws IOException {

        try{
            String filename = String.format("%s/%s", projId, file.getOriginalFilename());

            String contentType = file.getContentType();
            InputStream targetIS = file.getInputStream();
            byte[] bytes = IOUtils.toByteArray(targetIS);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .contentDisposition(contentType)
                    .build();

            amazonS3.putObject(putObjectRequest, AsyncRequestBody.fromBytes(bytes));
            return getPublicUrl(filename);
        } catch(IOException e) {
            logger.warn("파일 등록에 실패하셨습니다");
            return null;
        }
    }

    /**
     * url 반환 메소드
     * @param filename - 고유한 파일명
     * @return S3에 저장된 경로 url 반환
     * */
    private String getPublicUrl(String filename) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, regionStr, filename);
    }

}
