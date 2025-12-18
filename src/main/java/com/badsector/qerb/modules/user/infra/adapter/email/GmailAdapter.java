package com.badsector.qerb.modules.user.infra.adapter.email;

import com.badsector.qerb.modules.user.domain.port.out.EmailPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendVerificationEmail(String to, String token) {
        try {
            String verificationLink = baseUrl + "/api/v1/auth/verify?token=" + token;
            String emailContent = """
                    Hello,

                    Welcome to QERB! To activate your account, please verify your email address by clicking the link below:

                    %s

                    This link is valid for 24 hours.

                    Best regards,
                    The QERB Team
                    """.formatted(verificationLink);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Verify Your QERB Account");
            message.setText(emailContent);

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
        }
    }
}