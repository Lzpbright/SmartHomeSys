package com.lzp.smarthomesys;

import com.lzp.smarthomesys.utils.EMailUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@SpringBootTest
public class EMailTest {
//
//
////    public void sendEmail(String who, String content) {
////
////        Properties props = new Properties();
////        props.put("mail.smtp.auth", "true");
////        props.put("mail.smtp.starttls.enable", "true");
////        props.put("mail.smtp.host", host);
////        props.put("mail.smtp.port", port);
////
////        Session session = Session.getInstance(props,
////                new Authenticator() {
////                    protected PasswordAuthentication getPasswordAuthentication() {
////                        return new PasswordAuthentication(account, password);
////                    }
////                });
////
////        try {
////            Message message = new MimeMessage(session);
////            message.setFrom(new InternetAddress(account)); // 设置发送方邮箱地址
////            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(who)); // 设置收件人邮箱地址
////            message.setSubject("邮件主题"); // 设置邮件主题
////            message.setText(content); // 设置邮件内容
////            Transport.send(message); // 发送邮件
////            System.out.println("邮件发送成功。");
////        } catch (MessagingException e) {
////            System.out.println("邮件发送失败。" + e.getMessage());
////        }
////    }
//    @Test
//    void sendEMailTest(){
//        Random rand = new Random();
//        int randomNum = rand.nextInt(100000); // 生成0-99999的随机整数
//        String randomString = String.format("%05d", randomNum); // 转换为5位的字符串
//        EMailUtils.send("验证码", "168422513@qq.com", randomString, false);
//    }
}
