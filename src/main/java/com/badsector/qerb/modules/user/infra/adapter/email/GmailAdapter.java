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
    @Value("${app.base-url}")
    private String baseUrl;
    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendVerificationEmail(String to, String token) {
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        String subject = "Verify Your QERB Account";
        String body = """
                Hello,

                Welcome to QERB! To activate your account, please verify your email address by clicking the link below:

                %s

                This link is valid for 24 hours.

                Best regards,
                The QERB Team
                """.formatted(verificationLink);

        sendEmail(to, subject, body);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        String subject = "Password Reset Request";
        String body = """
                Hello,

                We received a request to reset your password.
                To reset your password, click the link below:

                %s

                This link is valid for 15 minutes.
                If you did not request a password reset, you can safely ignore this email.

                Best regards,
                The QERB Team
                """.formatted(resetLink);

        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent successfully to: {} | Subject: {}", to, subject);

        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
        }
    }
}