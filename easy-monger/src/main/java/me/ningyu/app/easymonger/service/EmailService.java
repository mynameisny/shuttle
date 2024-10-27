package me.ningyu.app.easymonger.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
    private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }
    
    
    public void sendSimpleMail(String to, String subject, String text)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("MS_jA3xWF@trial-pr9084z7kvvgw63d.mlsender.net");
        
        mailSender.send(message);
    }
}
