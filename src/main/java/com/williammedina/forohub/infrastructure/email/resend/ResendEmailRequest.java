package com.williammedina.forohub.infrastructure.email.resend;

import java.util.List;

public record ResendEmailRequest(
        String from,
        List<String> to,
        String subject,
        String html
) {
}