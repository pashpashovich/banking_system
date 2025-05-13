package by.bsuir.bank.entity;

import by.bsuir.bank.entity.enumeration.LoanStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "loan_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;
  private BigDecimal amount;
  @Column(name = "term_in_months")
  private int termInMonths;
  private String purpose;
  @Column(name = "request_date")
  private LocalDate requestDate;
  @Enumerated(EnumType.STRING)
  private LoanStatus status;
  private String reason;
}
