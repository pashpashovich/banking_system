package by.bsuir.bank.config;

import by.bsuir.bank.domain.MailProperties;
import jakarta.annotation.Resource;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguration {

  @Resource
  private MailProperties mailProperties;

  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl sender = new JavaMailSenderImpl();
    sender.setHost(mailProperties.getHost());
    sender.setPort(mailProperties.getPort());
    sender.setUsername(mailProperties.getUsername());
    sender.setPassword(mailProperties.getPassword());

    Properties props = sender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
    props.put("mail.smtp.ssl.trust", mailProperties.getHost());
    props.put("mail.debug", "true");

    return sender;
  }
}
