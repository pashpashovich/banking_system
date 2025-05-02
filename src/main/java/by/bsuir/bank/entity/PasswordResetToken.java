package by.bsuir.bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;
  private LocalDateTime expiryDate;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  public PasswordResetToken(User user) {
    this.user = user;
    this.token = UUID.randomUUID().toString();
    this.expiryDate = LocalDateTime.now().plusHours(1);
  }

  public PasswordResetToken() {

  }
}
