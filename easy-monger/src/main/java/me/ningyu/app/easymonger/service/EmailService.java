package me.ningyu.app.easymonger.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
    
    public void sendActivationEmail(String to, String activationLink)
    {
        try
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("激活您的账户");
            helper.setText("点击以下链接激活您的账户：<a href=\"" + activationLink + "\">激活链接</a>", true);
            
            // 发送邮件
            mailSender.send(message);
            System.out.println("邮件发送成功");
        }
        catch (Exception exception)
        {
            log.error("给{}发送激活邮件时出现错误", to, exception);
        }
    }
}
