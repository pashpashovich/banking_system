package ru.clevertec.bank.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.AccountDto;
import ru.clevertec.bank.domain.TransactionDTO;
import ru.clevertec.bank.entity.enumeration.TransactionType;
import ru.clevertec.bank.service.AccountService;
import ru.clevertec.bank.service.CurrencyConversionService;
import ru.clevertec.bank.service.TransactionService;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
            List<TransactionDTO> transactions = transactionService.findTransactionsByDateRangeAndAccount(startDate, endDate, accountNum);

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            String fontPath = "src/main/resources/fonts/DejaVuSans.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

            addHeader(document, font);
            addReportDetails(document, font, accountNum, month);
            addTransactionSummary(document, font, transactions, accountNum);
            addTransactionTable(document, font, transactions);

            document.close();
            return outputStream.toByteArray();
        }
    }


    public byte[] generateTransactionReport(LocalDate startDate1, LocalDate endDate1,String firstName, String secondName,String patronymicName) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            LocalDateTime startDate = startDate1.atStartOfDay();
            LocalDateTime endDate = endDate1.atTime(23, 59, 59);
            List<TransactionDTO> transactions = transactionService.findTransactionsByDateRange(startDate, endDate);
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            String fontPath = "src/main/resources/fonts/DejaVuSans.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
            addHeader(document, font);
            addReportDetails(document, font, startDate, endDate);
            addTransactionSummary(document, font, transactions);
            addTransactionTable(document, font, transactions);

            document.close();
            return outputStream.toByteArray();
        }
    }

    private void addHeader(Document document, PdfFont font) throws Exception {
        String imagePath = "src/main/resources/images/logo.png";
        Image logo = new Image(ImageDataFactory.create(imagePath))
                .scaleToFit(30, 30)
                .setFixedPosition(40, 750);
        document.add(logo);

        Paragraph bankName = new Paragraph("FinScope")
                .setFont(font)
                .setFontSize(24)
                .setBold()
                .setFontColor(ColorConstants.BLACK)
                .setFixedPosition(80, 747, 1000);
        document.add(bankName);

        document.add(new Paragraph("Отчет по транзакциям")
                .setFont(font)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setPaddingTop(35)
                .setMarginBottom(20));
    }

    private void addReportDetails(Document document, PdfFont font, String accountNum, int month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = LocalDateTime.now().format(formatter);

        document.add(new Paragraph("Дата отчета: " + formattedDate).setFont(font));
        document.add(new Paragraph("Номер счета: " + accountNum).setFont(font));
        document.add(new Paragraph("Месяц: " + month).setFont(font).setMarginBottom(10));
    }

    private void addReportDetails(Document document, PdfFont font, LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = LocalDateTime.now().format(formatter);

        document.add(new Paragraph("Дата отчета: " + formattedDate).setFont(font));
        document.add(new Paragraph("Дата начала: " + startDate.format(formatter)).setFont(font).setMarginBottom(10));
        document.add(new Paragraph("Дата конца: " + endDate.format(formatter)).setFont(font).setMarginBottom(10));
    }

    private void addTransactionSummary(Document document, PdfFont font, List<TransactionDTO> transactions, String accountNum) {
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
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2, 1}));
        table.addHeaderCell(new Paragraph("ID").setFont(font).setBold());
        table.addHeaderCell(new Paragraph("Сумма").setFont(font).setBold());
        table.addHeaderCell(new Paragraph("Валюта").setFont(font).setBold());
        table.addHeaderCell(new Paragraph("Дата").setFont(font).setBold());
        table.addHeaderCell(new Paragraph("Тип").setFont(font).setBold());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (TransactionDTO transaction : transactions) {
            table.addCell(new Paragraph(String.valueOf(transaction.getId())).setFont(font));
            table.addCell(new Paragraph(String.valueOf(transaction.getAmount())).setFont(font));
            table.addCell(new Paragraph(transaction.getCurrency()).setFont(font));
            table.addCell(new Paragraph(transaction.getTransactionTime().format(formatter)).setFont(font));
            table.addCell(new Paragraph(transaction.getTransactionType().toString()).setFont(font));
        }

        document.add(table);
    }
}
