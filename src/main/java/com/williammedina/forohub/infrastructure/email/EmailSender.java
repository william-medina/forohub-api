package com.williammedina.forohub.infrastructure.email;

public interface EmailSender {
    void sendEmail(String to, String subject, String title, String message, String buttonLabel, String url, String footer);
}
