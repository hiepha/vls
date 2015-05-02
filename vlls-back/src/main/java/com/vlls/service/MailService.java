package com.vlls.service;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.jpa.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@Configuration
public class MailService extends JavaMailSenderImpl {

    protected PropertyResolver props;

    @Autowired
    private UserService userService;

    @Autowired
    private LearningItemService learningItemService;

    @Resource(name = "vllsPropertyResolver")
    protected PropertyResolver vllsProperties;

    @Autowired
    public void init(@Qualifier("vllsPropertyResolver") PropertyResolver props) {
        this.props = props;
        this.setHost(this.props.getProperty("mail.server.host"));
        this.setPort(this.props.getProperty("mail.server.port", Integer.class));
        this.setUsername(this.props.getProperty("mail.server.username"));
        this.setPassword(this.props.getProperty("mail.server.password"));
        this.getJavaMailProperties().setProperty("mail.smtp.auth",
                this.props.getProperty("mail.smtp.auth"));
        this.getJavaMailProperties().setProperty("mail.smtp.starttls.enable",
                this.props.getProperty("mail.smtp.starttls.enable"));
    }

    public void sendMail(String recipientMail, String subject,
                         String htmlContent) throws MessagingException,
            UnsupportedEncodingException {
        MimeMessage mimeMessage = this.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage, true, "UTF-8");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setFrom(this.props.getProperty("mail.server.fromEmail"),
                this.props.getProperty("mail.server.fromAlias"));
        mimeMessageHelper.setTo(recipientMail);
        mimeMessageHelper.setText(htmlContent, true);

        this.send(mimeMessage);
    }

    public void validateEmail(String email) throws AddressException {
        new InternetAddress(email).validate();
    }

    @Scheduled(fixedRate = 900000)
    public void sendUserReviseItemList() throws IOException, UnauthenticatedException, MessagingException {
        List<User> users = userService.getAllUsers();
        String tempFile = vllsProperties.getProperty("mail.template.revision");
        String text = new String(Files.readAllBytes(Paths.get(tempFile)), StandardCharsets.UTF_8);
        for (User user : users) {
            List<Object[]> items = learningItemService.getReviseItem(user.getId());
            if (items.size() > 0) {
                String htmlContent = text.replaceFirst("%username%", user.getName());
                String words = "";
                for (Object[] item : items) {
                    words += "<tr><td style='padding:10px;border:0 solid #ddd'>" + item[0] + "</td>"
                            + "<td style='padding:10px;border:0 solid #ddd'>" + item[2] + "</td></tr>";
                }
                htmlContent = htmlContent.replaceFirst("%words%", words);
                sendMail(user.getEmail(), items.size() + " items can be revised", htmlContent);
            }
        }
    }

    public void sendUserRecallMail() throws IOException, UnauthenticatedException, MessagingException, NoInstanceException {
        List<Object[]> users = userService.getLastLogin();
        String tempFile = vllsProperties.getProperty("mail.template.revision");
        String text = new String(Files.readAllBytes(Paths.get(tempFile)), StandardCharsets.UTF_8);
        for (Object[] user : users) {
            String htmlContent = text.replaceFirst("%username%", (String) user[0]);
            String words = "";
            htmlContent = htmlContent.replaceFirst("%words%", words);
            sendMail("ngkanhtuan@gmail.com", " items can be revised", htmlContent);
        }
    }

}
