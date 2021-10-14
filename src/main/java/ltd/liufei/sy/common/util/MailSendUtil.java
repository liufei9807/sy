package ltd.liufei.sy.common.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ltd.liufei.sy.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 邮件发送工具类
 */
@Component
public class MailSendUtil {

    @Autowired
    JavaMailSender javaMailSender;


    public void sendMail(MailEntity mailEntity) {
        checkMail(mailEntity);
        sendMimeMail(mailEntity);
    }

    //检测邮件信息类
    private void checkMail(MailEntity mailEntity) {
        if (CharSequenceUtil.isEmpty(mailEntity.getTo())) {
            throw new BaseException("邮件收信人不能为空");
        }
        if (CharSequenceUtil.isEmpty(mailEntity.getSubject())) {
            throw new BaseException("邮件主题不能为空");
        }
        if (CharSequenceUtil.isEmpty(mailEntity.getText())) {
            throw new BaseException("邮件内容不能为空");
        }
    }

    private void sendMimeMail(MailEntity mailEntity) {
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);//true表示支持复杂类型
            messageHelper.setFrom(mailEntity.getFrom());//邮件发信人
            messageHelper.setTo(mailEntity.getTo().split(","));//邮件收信人
            messageHelper.setSubject(mailEntity.getSubject());//邮件主题
            messageHelper.setText(mailEntity.getText());//邮件内容
            if (CharSequenceUtil.isNotEmpty(mailEntity.getCc())) {//抄送
                messageHelper.setCc(mailEntity.getCc().split(","));
            }
            if (CharSequenceUtil.isNotEmpty(mailEntity.getBcc())) {//密送
                messageHelper.setBcc(mailEntity.getBcc().split(","));
            }
            if (mailEntity.getMultipartFiles() != null) {//添加邮件附件
                for (MultipartFile multipartFile : mailEntity.getMultipartFiles()) {
                    if (multipartFile != null && multipartFile.getOriginalFilename() != null) {
                        messageHelper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
                    }
                }
            }
            if (mailEntity.getSentDate() != null) {//发送时间
                mailEntity.setSentDate(new Date());
                messageHelper.setSentDate(mailEntity.getSentDate());
            }
            javaMailSender.send(messageHelper.getMimeMessage());//正式发送邮件
            mailEntity.setStatus("ok");
        } catch (Exception e) {
            throw new BaseException(e);//发送失败
        }
    }


    @Data
    @Builder
    static class MailEntity {

        private String id;//邮件id

        private String from;//邮件发送人

        private String to;//邮件接收人

        private String subject;//邮件主题

        private String text;//邮件内容

        private Date sentDate;//发送时间

        private String cc; //抄送

        private String bcc; //密送

        private String status; //状态

        private String error; //报错信息

        @JsonIgnore
        private MultipartFile[] multipartFiles;//邮件附件

        public String getFrom() {
            return from == null || "".equals(from) ? "me@liufei.ltd" : from;
        }
    }

}
