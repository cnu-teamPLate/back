package com.cnu.teamProj.teamProj.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    /**
     * S3에 파일 업로드해주는 메소드
     * @param file - multipart/form-data 방식으로 파일에 대한 요청값을 받음
     * @return 파일을 업로드한 후 url 값을 반환함
     * */
    public String uploadFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename(); //고유한 파일 이름 생성

        //메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        //S3에 파일 업로드 요청 생성
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, filename, file.getInputStream(), metadata);

        //S3에 파일 업로드
        amazonS3.putObject(putObjectRequest);

        return getPublicUrl(filename);
    }

    /**
     * url 반환 메소드
     * @param filename - 고유한 파일명
     * @return S3에 저장된 경로 url 반환
     * */
    private String getPublicUrl(String filename) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), filename);
    }
}
