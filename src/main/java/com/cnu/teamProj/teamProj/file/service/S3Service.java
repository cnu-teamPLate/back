package com.cnu.teamProj.teamProj.file.service;

import com.cnu.teamProj.teamProj.file.dto.FileDto;
import com.cnu.teamProj.teamProj.file.repository.DocsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3AsyncClient amazonS3;
    private final DocsRepository docsRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${spring.cloud.aws.region.static}")
    private String regionStr;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);


    /**
     * S3에 파일 업로드해주는 메소드
     * @param file - multipart/form-data 방식으로 파일에 대한 요청값을 받음
     * @param projId - 어떤 프로젝트와 연관된 파일인지 저장하기 위한 프로젝트 아이디
     * @return 파일을 업로드한 후 url 값을 반환함
     * */
    public FileDto uploadFile(MultipartFile file, String projId) throws IOException {

        try{
            String originalFilename = file.getOriginalFilename();
            int cnt = docsRepository.findDocsByProjIdAndFilename(projId, originalFilename).size();
            if(cnt > 0) {
                assert originalFilename != null;
                String[] temps = originalFilename.split("\\.");
                if(temps.length != 2) return null;
                originalFilename = String.format("%s(%d).%s", temps[0], cnt+1, temps[1]);
            }

            String filename = String.format("%s/%s", projId, originalFilename);


            String contentType = file.getContentType();
            InputStream targetIS = file.getInputStream();
            byte[] bytes = IOUtils.toByteArray(targetIS);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .contentDisposition(contentType)
                    .build();


            PutObjectResponse resp = amazonS3.putObject(putObjectRequest, AsyncRequestBody.fromBytes(bytes)).get();
            String url = getPublicUrl(filename);
            logger.info("파일 등록 결과: {}", resp);
            return new FileDto(url, file.getOriginalFilename());
        } catch(IOException e) {
            logger.warn("파일 등록에 실패하셨습니다: {}", e.getMessage());
            return null;
        } catch(Exception e) {
            logger.error("결과 에러: {}", e.getMessage());
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

    /**
     * s3 버킷에서 파일 삭제
     * @param filename 삭제하고자 하는 파일의 키값
     * @return 결괏값
     */
    public boolean deleteFile(String filename) {
        try{
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .versionId(null)
                    .build();

            logger.info("전달된 파일명: {}", filename);
            DeleteObjectResponse response = amazonS3.deleteObject(deleteObjectRequest).get();
            logger.info("Responses: {}", response);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(e.getMessage());
            logger.error("에러 발생: {}", e.getMessage());
            return false;
        }

    }

}
