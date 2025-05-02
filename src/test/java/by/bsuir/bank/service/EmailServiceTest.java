package by.bsuir.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private EmailService emailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldSendEmail() {
    // Given
    String to = "user@example.com";
    String subject = "Test Subject";
    String text = "This is a test message";

    // When
    emailService.sendEmail(to, subject, text);

    // Then
    ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(captor.capture());

    SimpleMailMessage sentMessage = captor.getValue();
    assertEquals("info@trial-z3m5jgrw55zldpyo.mlsender.net", sentMessage.getFrom());
    assertEquals(to, sentMessage.getTo()[0]);
    assertEquals(subject, sentMessage.getSubject());
    assertEquals(text, sentMessage.getText());
  }
}

