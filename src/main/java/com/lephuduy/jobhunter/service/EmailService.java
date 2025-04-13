package com.lephuduy.jobhunter.service;

import com.lephuduy.jobhunter.domain.Job;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {
    private JavaMailSender emailSender;

    private TemplateEngine templateEngine;

    private JobService jobService;

    public EmailService (JavaMailSender emailSender, TemplateEngine templateEngine, JobService jobService) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.jobService = jobService;
    }

    public void sendSimpleEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("duylephu204@gmail.com");
        message.setSubject("Testing send email with java spring");
        message.setText("Hello gmail spring boot");
        emailSender.send(message);
    }

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart,
                              boolean isHtml) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.emailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
                    isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.emailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }

    public void sendEmailFromTemplateSync(String to, String subject, String
            templateName) {
        List<Job> listJob = this.jobService.findAllJob();
        Context context = new Context();
        context.setVariable("jobs", listJob);
        String content = this.templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }
}
