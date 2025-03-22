package com.cnu.teamProj.teamProj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.crt.S3CrtAsyncClient;

import java.time.Duration;

@Configuration
public class S3Config {
    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String accessSecret;
    @Value("${spring.cloud.aws.region.static}")
    private String regionStr;

    @Bean
    public S3AsyncClient amazonS3(){
        //접근 키와 시크릿 키를 통해 자격증명 생성 = awsCredentials
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, accessSecret);
        final Region region = Region.of(regionStr);
        //위에서 생성한 자격증명을 바탕으로 client 객체 생성
        return S3CrtAsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(region)
                .build();
    }
}