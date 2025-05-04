package com.cnu.teamProj.teamProj.util;

import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.TimedRetryAlgorithm;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import org.threeten.bp.Duration;

public class STTUtil {
    public static void asyncRecognizeGcs(String gcsUri) throws Exception {
        SpeechSettings.Builder speechSettings = SpeechSettings.newBuilder();
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
        speechSettings.longRunningRecognizeOperationSettings().setPollingAlgorithm(timedRetryAlgorithm);

        try(SpeechClient speech = SpeechClient.create(speechSettings.build())) {
//            RecognitionConfig config = RecognitionConfig.newBuilder()
//                    .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
//                    .setLanguageCode("");
        }
    }
}
