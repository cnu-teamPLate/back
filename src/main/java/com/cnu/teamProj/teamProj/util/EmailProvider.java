package com.cnu.teamProj.teamProj.util;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProvider {
    private final JavaMailSender javaMailSender;
    private final String SUBJECT = "[teamPlate] 비밀번호 안내 메일입니다";

    /**
     * 임시 비밀번호 생성 함수
     * @return 임시 비밀번호
     */
    public String createRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        
        StringBuilder sb = new StringBuilder(10);
        for(int i=0; i<10; i++) {
            int idx = random.nextInt(characters.length());
            sb.append(characters.charAt(idx));
        }
        return sb.toString();


    }
    public String sendCertificationMail(String email) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);

            String password = createRandomPassword();
            String htmlContent = createMailMessage(password);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);

            return password;
        } catch(Exception e) {
            log.error("메일을 보내는 중 에러 발생: {}", e.getMessage());
            return null;
        }
    }

    private String createMailMessage(String password) {
        String certificationMessage = "";
        certificationMessage += "<h1 style = 'text-align: center;'>[teamPlate 임시 비밀번호 발송]</h1>";
        certificationMessage += "<h3 style = 'text-align: center;'>임시 비밀번호 : <strong style = 'font-size: 32px; letter-spacing: 8px;'>"+password+"</strong></h3>";
        certificationMessage += "<span>보안을 위해 비밀번호를 변경하세요</span>";
        return certificationMessage;
    }
}
