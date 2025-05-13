package by.bsuir.bank.service;

import by.bsuir.bank.domain.AccountDto;
import by.bsuir.bank.domain.TransactionDTO;
import by.bsuir.bank.entity.enumeration.TransactionType;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final AccountService accountService;
  private final TransactionService transactionService;
  private final CurrencyConversionService currencyConversionService;

  public byte[] generateMonthlyTransactionReport(String accountNum, int month) throws Exception {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      AccountDto account = accountService.getAccountById(Long.valueOf(accountNum));
      if (account == null) {
        throw new RuntimeException("Account not found");
      }

      LocalDateTime startDate = YearMonth.of(LocalDate.now().getYear(), month).atDay(1).atStartOfDay();
      LocalDateTime endDate = YearMonth.of(LocalDate.now().getYear(), month).atEndOfMonth().atTime(23, 59, 59);
      List<TransactionDTO> transactions = transactionService.findTransactionsByDateRangeAndAccount(startDate, endDate,
          accountNum);

      PdfWriter writer = new PdfWriter(outputStream);
      PdfDocument pdfDoc = new PdfDocument(writer);
      Document document = new Document(pdfDoc);

      String fontPath = "src/main/resources/fonts/DejaVuSans.ttf";
      PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

      addHeader(document, font, "Выписка со счёта");
      addReportDetails(document, font, accountNum, month);
      addTransactionSummary(document, font, transactions, accountNum);
      addTransactionTable(document, font, transactions);

      document.close();
      return outputStream.toByteArray();
    }
  }


  public byte[] generateTransactionReport(LocalDate startDate1, LocalDate endDate1, String firstName, String secondName,
      String patronymicName) throws Exception {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      LocalDateTime startDate = startDate1.atStartOfDay();
      LocalDateTime endDate = endDate1.atTime(23, 59, 59);
      List<TransactionDTO> transactions = transactionService.findTransactionsByDateRange(startDate, endDate);
      PdfWriter writer = new PdfWriter(outputStream);
      PdfDocument pdfDoc = new PdfDocument(writer);
      Document document = new Document(pdfDoc);
      String fontPath = "src/main/resources/fonts/DejaVuSans.ttf";
      PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
      addHeader(document, font, "Отчет по транзакционной активности клиентов");
      addReportDetails(document, font, startDate, endDate, firstName, secondName, patronymicName);
      addTransactionSummary(document, font, transactions);
      addTransactionTable(document, font, transactions);

      document.close();
      return outputStream.toByteArray();
    }
  }

    private void addHeader(Document document, PdfFont font, String name) throws Exception {
        String imagePath = "src/main/resources/images/logo.png";
        Image logo = new Image(ImageDataFactory.create(imagePath))
            .scaleToFit(30, 30)
            .setMarginRight(10);

        Paragraph header = new Paragraph()
            .add(logo)
            .add(" FinScope")
            .setFont(font)
            .setFontSize(24)
            .setBold()
            .setFontColor(ColorConstants.BLACK)
            .setMarginBottom(10);

        document.add(header);

        document.add(new Paragraph(name)
            .setFont(font)
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setMarginBottom(20));
    }


    private void addReportDetails(Document document, PdfFont font, String accountNum, int month) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    String formattedDate = LocalDateTime.now().format(formatter);

    document.add(new Paragraph("Дата отчета: " + formattedDate).setFont(font));
    document.add(new Paragraph("Номер счета: " + accountNum).setFont(font));
    document.add(new Paragraph("Месяц: " + month).setFont(font).setMarginBottom(10));
  }

  private void addReportDetails(Document document, PdfFont font, LocalDateTime startDate, LocalDateTime endDate,
      String firstName, String secondName, String patronymicName) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    String formattedDate = LocalDateTime.now().format(formatter);

    document.add(new Paragraph("Дата отчета: " + formattedDate).setFont(font));
    document.add(new Paragraph("ФИО аналитика: " + secondName + " " + firstName + " " + patronymicName).setFont(font));
    document.add(new Paragraph("Дата начала: " + startDate.format(formatter)).setFont(font).setMarginBottom(10));
    document.add(new Paragraph("Дата конца: " + endDate.format(formatter)).setFont(font).setMarginBottom(10));
  }

  private void addTransactionSummary(Document document, PdfFont font, List<TransactionDTO> transactions,
      String accountNum) {
    BigDecimal maxTransaction = BigDecimal.ZERO;
    BigDecimal minTransaction = new BigDecimal(Integer.MAX_VALUE);
    BigDecimal totalAmount = BigDecimal.ZERO;
    BigDecimal totalDeposits = BigDecimal.ZERO;
    BigDecimal totalWithdrawals = BigDecimal.ZERO;
    int transactionCount = 0;

    for (TransactionDTO transaction : transactions) {
      BigDecimal amountInByn = currencyConversionService.convert(
          BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(), "BYN");

      maxTransaction = maxTransaction.max(amountInByn);
      minTransaction = minTransaction.min(amountInByn);
      totalAmount = totalAmount.add(amountInByn);
      transactionCount++;

      if (transaction.getTransactionType().equals(TransactionType.DEPOSIT) ||
          (transaction.getTransactionType().equals(TransactionType.TRANSFER) &&
              transaction.getRecipientAccountId().equals(Long.valueOf(accountNum)))) {
        totalDeposits = totalDeposits.add(amountInByn);
      }
      if (transaction.getTransactionType().equals(TransactionType.WITHDRAWAL) ||
          (transaction.getTransactionType().equals(TransactionType.TRANSFER) &&
              transaction.getSenderAccountId().equals(Long.valueOf(accountNum)))) {
        totalWithdrawals = totalWithdrawals.add(amountInByn);
      }
    }

    document.add(new Paragraph("Транзакция с максимальной суммой: " + maxTransaction + " BYN").setFont(font));
    document.add(new Paragraph("Транзакция с минимальной суммой: " + minTransaction + " BYN").setFont(font));
    document.add(new Paragraph("Общая сумма транзакций: " + totalAmount + " BYN").setFont(font));
    document.add(new Paragraph("Общая сумма доходов: " + totalDeposits + " BYN").setFont(font));
    document.add(new Paragraph("Общая сумма расходов: " + totalWithdrawals + " BYN").setFont(font));
    document.add(new Paragraph("Общее количество транзакций: " + transactionCount).setFont(font).setMarginBottom(10));
  }

  private void addTransactionSummary(Document document, PdfFont font, List<TransactionDTO> transactions) {
    BigDecimal maxTransaction = BigDecimal.ZERO;
    BigDecimal minTransaction = new BigDecimal(Integer.MAX_VALUE);
    BigDecimal totalAmount = BigDecimal.ZERO;
    int transactionCount = 0;
    for (TransactionDTO transaction : transactions) {
      BigDecimal amountInByn = currencyConversionService.convert(
          BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(), "BYN");

      maxTransaction = maxTransaction.max(amountInByn);
      minTransaction = minTransaction.min(amountInByn);
      totalAmount = totalAmount.add(amountInByn);
      transactionCount++;
    }

    document.add(new Paragraph("Транзакция с максимальной суммой: " + maxTransaction + " BYN").setFont(font));
    document.add(new Paragraph("Транзакция с минимальной суммой: " + minTransaction + " BYN").setFont(font));
    document.add(new Paragraph("Общая сумма транзакций: " + totalAmount + " BYN").setFont(font));
    document.add(new Paragraph("Общее количество транзакций: " + transactionCount).setFont(font).setMarginBottom(10));
  }

    private void addTransactionTable(Document document, PdfFont font, List<TransactionDTO> transactions) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 3, 2}))
            .useAllAvailableWidth()
            .setMarginTop(10)
            .setMarginBottom(10);

        table.addHeaderCell(new Paragraph("ID").setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(new Paragraph("Сумма").setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(new Paragraph("Валюта").setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(new Paragraph("Дата").setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(new Paragraph("Тип").setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (TransactionDTO transaction : transactions) {
            table.addCell(new Paragraph(String.valueOf(transaction.getId())).setFont(font).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Paragraph(String.valueOf(transaction.getAmount())).setFont(font).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Paragraph(transaction.getCurrency()).setFont(font).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Paragraph(transaction.getTransactionTime().format(formatter)).setFont(font).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Paragraph(transaction.getTransactionType().getDisplayName()).setFont(font).setTextAlignment(TextAlignment.CENTER));
        }

        document.add(table);
    }
}
