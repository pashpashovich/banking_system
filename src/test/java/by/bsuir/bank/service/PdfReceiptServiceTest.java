package by.bsuir.bank.service;

import static org.assertj.core.api.Assertions.assertThat;

import by.bsuir.bank.domain.TransactionDTO;
import by.bsuir.bank.entity.enumeration.TransactionType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PdfReceiptServiceTest {

  private PdfReceiptService pdfReceiptService;

  @BeforeEach
  void setUp() {
    pdfReceiptService = new PdfReceiptService();
  }

  @Test
  void shouldGenerateValidPdfBytes() throws Exception {
    // Given
    TransactionDTO transaction = new TransactionDTO();
    transaction.setId(101L);
    transaction.setSenderAccountId(111111L);
    transaction.setRecipientAccountId(222222L);
    transaction.setAmount(250.75);
    transaction.setCurrency("BYN");
    transaction.setTransactionType(TransactionType.DEPOSIT);
    transaction.setTransactionTime(LocalDateTime.now());

    // When
    byte[] pdfBytes = pdfReceiptService.generateReceiptPdf(transaction);

    // Then
    assertThat(pdfBytes).isNotNull();
    assertThat(pdfBytes.length).isGreaterThan(100);
    assertThat(new String(pdfBytes)).contains("%PDF");
  }
}
