package com.waither.domain.user.service.commandService;

import com.waither.global.exception.CustomException;
import com.waither.global.response.UserErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    //의존성 주입을 통해서 필요한 객체를 가져온다.
    private final JavaMailSender emailSender;
    // 타임리프를사용하기 위한 객체를 의존성 주입으로 가져온다
    private final SpringTemplateEngine templateEngine;
    private String authNum; //랜덤 인증 코드


    // 인증 번호 발송 폼 생성
    private MimeMessage createEmailForm(String email, String title, String mailType, String code) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            message.setFrom(new InternetAddress("Waither","Waither"));//보내는 사람
            //email-config에 설정한 자신의 이메일 주소(보내는 사람)
            message.addRecipients(MimeMessage.RecipientType.TO, email); //보낼 이메일 설정

            message.setSubject(title); //제목 설정
            message.setText(setContext(mailType, code), "utf-8", "html");

            return message;
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new CustomException(UserErrorCode.NO_SUCH_ALGORITHM);
        }
    }

    //실제 메일 전송
    public void sendEmail(String email, String title, String mailType, String code) {

        try {
            MimeMessage emailForm = createEmailForm(email, title, mailType, code);
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " + "title: {}, authcode: {}",
                    email, title, code);
            throw new CustomException(UserErrorCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    //타임리프를 이용한 context 설정
    public String setContext(String mailType, String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(mailType, context); //mail.html
    }


//    // 임시 비밀번호 발송 폼 생성
//    private MimeMessage createTempPasswordMessage(String Email, String title, String tempPassword) {
//        MimeMessage  message = emailSender.createMimeMessage();
//        try {
//            message.addRecipients(MimeMessage.RecipientType.TO, Email); // 받는 사람 추가
//            message.setSubject(title); // 제목 설정
//
//            String msgg = "<div style='background-color:#f7f7f7; padding:20px;'>";
//            msgg += "<h1 style='color:#333; font-family: Arial, sans-serif; margin-bottom:20px;'>안녕하세요, 임시 비밀번호 안내입니다.</h1>";
//            msgg += "<p style='font-size: 16px; color:#666; margin-bottom:20px;'>임시 비밀번호를 안전하게 보관하세요.</p>";
//            msgg += "<div style='background-color:#fff; border:1px solid #ccc; padding: 20px; text-align: center; font-family:Verdana, Geneva, sans-serif;'>";
//            msgg += "<h3 style='color:#5189F6; font-size: 24px; margin-bottom: 20px;'>임시 비밀번호</h3>";
//            msgg += "<div style='font-size:20px;'><strong>CODE: " + tempPassword + "</strong></div>";
//            msgg += "</div></div>";
//
//            message.setText(msgg, "utf-8", "html"); // 내용 설정
//            message.setFrom(new InternetAddress("Weither", "Weither")); // 보내는 사람 설정
//
//            // 이메일 보내기
//            return message;
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
//        }
//    }
//
//    // 이메일 전송 메소드
//    public void sendAuthEmail(String email, String title, String Auth) {
//
//    }
//
//    public void sendTempPasswordEmail(String email, String title, String TempPassword){
//
//        try {
//            MimeMessage message = createTempPasswordMessage(email, title, TempPassword);
//            emailSender.send(message);
//        } catch (RuntimeException e) {
//            log.debug("MailService.sendEmail exception occur toEmail: {}, " + "title: {}, authcode: {}",
//                    email, title, TempPassword);
//            throw new CustomException(ErrorCode.UNABLE_TO_SEND_EMAIL);
//        }
//    }

}