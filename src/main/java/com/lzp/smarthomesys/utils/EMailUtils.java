package com.lzp.smarthomesys.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Slf4j
@Component
public class EMailUtils {
    @Value("${spring.mail.username}")
    private String account;
    @Resource
    private JavaMailSender javaMailSender;

    // 维护本类的一个静态变量
    private static EMailUtils eMailUtils;

    // 初始化的时候，将本类中的成员变量赋值给静态的本类变量
    @PostConstruct
    private void init(){
        eMailUtils = this;
        eMailUtils.javaMailSender = this.javaMailSender;
        eMailUtils.account = this.account;
    }

    /**
     * 发送邮件
     * @param subject 邮件主题
     * @param to 发送目的邮箱
     * @param content 内容
     * @param isHtml 是否发送html内容
     */
    public static void send(String subject, String to, String content, boolean isHtml){
        JavaMailSender javaMailSender = eMailUtils.javaMailSender;  // 使用本类维护的静态变量
        String account = eMailUtils.account;
        MimeMessage mailMessage = javaMailSender.createMimeMessage();   // 信息对象
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage);  // 信息helper类
        try{
            // 设置发送人
            helper.setFrom(account);
            // 设置发送目标
            helper.setTo(to);
            // 设置邮箱主题
            helper.setSubject(subject);
            // 设置日期
            helper.setSentDate(new Date());
            // 设置内容和内容种类
            helper.setText(content, isHtml);
            // 发送邮箱
            javaMailSender.send(mailMessage);
        } catch (MessagingException e) {
            log.info("邮件发送失败", e);
        }
    }
}
