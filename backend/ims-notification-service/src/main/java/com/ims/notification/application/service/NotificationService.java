package com.ims.notification.application.service;

import com.ims.notification.infrastructure.config.NotificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;
    private final NotificationProperties properties;

    public NotificationService(JavaMailSender mailSender, NotificationProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    public void sendLowStockAlert(String recipientEmail, String itemName, int currentQty, int reorderLevel) {
        String subject = "[IMS] Low Stock Alert: " + itemName;
        String body = String.format(
                "Item '%s' is at or below reorder level.%nCurrent quantity: %d%nReorder level: %d%n%nPlease replenish stock.",
                itemName, currentQty, reorderLevel
        );
        sendEmail(recipientEmail, subject, body);
    }

    public void sendTransferFailedAlert(String recipientEmail, String transferId, String reason) {
        String subject = "[IMS] Transfer Failed: " + transferId;
        String body = String.format(
                "Stock transfer %s has failed.%nReason: %s%n%nPlease review and retry the transfer.",
                transferId, reason
        );
        sendEmail(recipientEmail, subject, body);
    }

    public void sendMarketSessionNotification(String recipientEmail, String marketName, String action) {
        String subject = String.format("[IMS] Market '%s' %s", marketName, action);
        String body = String.format("Market '%s' has been %s.", marketName, action.toLowerCase());
        sendEmail(recipientEmail, subject, body);
    }

    public void sendGenericNotification(String recipientEmail, String subject, Map<String, Object> payload) {
        String body = "Event details:\n" + payload.toString();
        sendEmail(recipientEmail, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (!properties.isEmailEnabled()) {
            log.info("Email disabled. Would have sent to {}: {}", to, subject);
            return;
        }

        int attempt = 0;
        while (attempt < properties.getMaxRetries()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(properties.getFromEmail());
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("Email sent to {} | subject: {}", to, subject);
                return;
            } catch (MailException e) {
                attempt++;
                log.warn("Email send attempt {}/{} failed: {}", attempt, properties.getMaxRetries(), e.getMessage());
                if (attempt < properties.getMaxRetries()) {
                    try {
                        Thread.sleep(properties.getRetryBackoffMs() * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        log.error("Failed to send email to {} after {} attempts", to, properties.getMaxRetries());
    }
}
