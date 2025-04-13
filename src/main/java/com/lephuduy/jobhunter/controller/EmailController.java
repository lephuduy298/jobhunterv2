package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService){
        this.emailService = emailService;
    }

    @GetMapping("/email")
    public String sendEmail() {
//        this.emailService.sendSimpleEmail();
        this.emailService.sendEmailFromTemplateSync("duylephu204@gmail.com", "Job Hunter", "job");
        return "ok";
    }
}
