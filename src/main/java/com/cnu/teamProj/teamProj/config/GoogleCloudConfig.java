package com.cnu.teamProj.teamProj.config;

import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.TimedRetryAlgorithm;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.stub.SpeechStubSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.threeten.bp.Duration;

import java.io.IOException;

@Configuration
@Slf4j
public class GoogleCloudConfig {
    @Value("${spring.cloud.gcp.credentials.location}")
    Resource gcsCredentials;


    @Bean
    public SpeechSettings speechSettings() throws IOException {
        log.info("speechSettings 실행: {}", gcsCredentials.contentLength());

        TimedRetryAlgorithm timedRetryAlgorithm = OperationTimedPollAlgorithm.create(
                RetrySettings.newBuilder()
                        .setInitialRetryDelay(Duration.ofMillis(500L))
                        .setRetryDelayMultiplier(1.5)
                        .setMaxRetryDelay(Duration.ofMillis(5000L))
                        .setInitialRpcTimeout(Duration.ZERO)
                        .setRpcTimeoutMultiplier(1.0)
                        .setMaxRpcTimeout(Duration.ZERO)
                        .setTotalTimeout(Duration.ofHours(24L))
                        .build());

        try{

            SpeechSettings.Builder speechSettings = SpeechSettings.newBuilder();

            speechSettings
                    .setCredentialsProvider(()-> GoogleCredentials.fromStream(gcsCredentials.getInputStream())) //인증 정보 설정
                    .longRunningRecognizeOperationSettings()
                    .setPollingAlgorithm(timedRetryAlgorithm);
            return speechSettings.build();

        } catch(Exception e) {
            log.error("구글 환경 설정 중 에러 발생: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    public SpeechStubSettings speechStubSettings() {
        log.info("speechSettings 실행: {}", gcsCredentials);

        try{
            return SpeechStubSettings.newBuilder()
                    .setCredentialsProvider(()->GoogleCredentials.fromStream(gcsCredentials.getInputStream()))
                    .build();
        } catch(Exception e) {
            log.error("구글 환경 설정 중 에러 발생: {}", e.getMessage());
            return null;
        }
    }
}
