package com.cnu.teamProj.teamProj.util;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class STTUtil {
    private final SpeechSettings speechSettings;
    public String asyncRecognizeGcs(MultipartFile file) throws Exception {
        log.info("asyncRecognizeGcs 호출");
        log.info("파일형식: {}", file.getContentType());

        byte[] audioBytes = file.getBytes();
        ByteString audioData = ByteString.copyFrom(audioBytes);

        String fileType = file.getContentType();
        RecognitionConfig.AudioEncoding encodingType;
        if(fileType.equalsIgnoreCase("audio/wave")) {
            encodingType = RecognitionConfig.AudioEncoding.LINEAR16;
        } else {
            encodingType = RecognitionConfig.AudioEncoding.FLAC;
        }

        try(SpeechClient speech = SpeechClient.create(speechSettings)) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(encodingType)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(48000)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioData).build();


            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speech.longRunningRecognizeAsync(config, audio);
            while(!response.isDone()) {
                log.info("구글 STT 실행중 Waiting for response");
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();

            String text = "";
            for(SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                text += alternative.getTranscript();
                log.info("Transcription: {}", alternative.getTranscript());
            }
            return text;
        }
    }
}
