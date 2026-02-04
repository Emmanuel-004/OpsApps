package com.dansmultipro.opsapps.util;

import com.dansmultipro.opsapps.model.Transaction;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmailNotification(String to, String subject, String body, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("no-reply@payku.com");

            helper.setText(body, isHtml);

            mailSender.send(message);
        } catch (Exception e){
            throw  new RuntimeException("Message send failed");
        }
    }

    public String buildActivationEmail(String name, String activationLink) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("activationLink", activationLink);
        context.setVariable("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        return templateEngine.process("email/activationEmail", context);
    }

    public String buildTransactionCreatedEmail(Transaction transaction) {
        Context context = new Context();
        context.setVariable("name", transaction.getCustomer().getUserName());
        context.setVariable("transactionCode", transaction.getCode());
        context.setVariable("virtualAccount", transaction.getVirtualAccount());
        context.setVariable("productName", transaction.getProduct().getName());
        context.setVariable("paymentGateaway", transaction.getPaymentGateway().getName());
        context.setVariable("amount", transaction.getNominal());
        context.setVariable("status", transaction.getStatus().getName());

        return templateEngine.process("email/transactionCreatedEmail", context);
    }

    public String buildTransactionUpdatedEmail(Transaction transaction) {
        Context context = new Context();
        context.setVariable("name", transaction.getCustomer().getUserName());
        context.setVariable("transactionCode", transaction.getCode());
        context.setVariable("virtualAccount", transaction.getVirtualAccount());
        context.setVariable("productName", transaction.getProduct().getName());
        context.setVariable("paymentGateaway", transaction.getPaymentGateway().getName());
        context.setVariable("amount", transaction.getNominal());
        context.setVariable("status", transaction.getStatus().getName());

        return templateEngine.process("email/transactionUpdatedEmail", context);
    }
}
