package ru.clevertec.bank.controller;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.bank.domain.TransactionDTO;
import ru.clevertec.bank.entity.Transaction;
import ru.clevertec.bank.entity.enumeration.TransactionType;
import ru.clevertec.bank.mapper.TransactionMapper;
import ru.clevertec.bank.request.AccountTransactionStatsDTO;
import ru.clevertec.bank.request.DailyTransactionStats;
import ru.clevertec.bank.service.CurrencyConversionService;
import ru.clevertec.bank.service.TransactionService;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrencyConversionService currencyConversionService;
    private final TransactionMapper transactionMapper;

    @PostMapping("/")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        TransactionDTO savedTransaction = transactionService.createTransaction(transaction, transactionDTO.getSenderAccountId(), transactionDTO.getRecipientAccountId());
        return ResponseEntity.ok(savedTransaction);
    }

    @GetMapping("/receipt/{transactionId}")
    public ResponseEntity<byte[]> generateReceipt(@PathVariable Long transactionId) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            TransactionDTO transaction = transactionService.findTransactionById(transactionId);
            if (transaction == null) {
                return ResponseEntity.notFound().build();
            }

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            String fontPath = "src/main/resources/fonts/DejaVuSans.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

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
                    .setFixedPosition(80,747,1000);
            document.add(bankName);

            Paragraph title = new Paragraph("Чек")
                    .setFont(font)
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPaddingTop(35)
                    .setMarginBottom(30)
                    .setFontColor(ColorConstants.BLACK);
            document.add(title);

            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

            table.addCell(new Paragraph("ID транзакции:").setFont(font).setBold());
            table.addCell(new Paragraph(String.valueOf(transaction.getId())).setFont(font));

            table.addCell(new Paragraph("Тип транзакции:").setFont(font).setBold());
            table.addCell(new Paragraph(" " + transaction.getTransactionType()).setFont(font));

            table.addCell(new Paragraph("Номер счета отправителя:").setFont(font).setBold());
            table.addCell(new Paragraph(" " + transaction.getSenderAccountId()).setFont(font));

            table.addCell(new Paragraph("Номер счета получателя:").setFont(font).setBold());
            table.addCell(new Paragraph(" " + transaction.getRecipientAccountId()).setFont(font));

            table.addCell(new Paragraph("Сумма:").setFont(font).setBold());
            table.addCell(new Paragraph(transaction.getAmount() + " " + transaction.getCurrency()).setFont(font));

            table.addCell(new Paragraph("Дата и время:").setFont(font).setBold());
            table.addCell(new Paragraph(transaction.getTransactionTime().toString()).setFont(font));

            document.add(table);
            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "receipt_" + transactionId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{accountNum}/{month}/daily")
    public ResponseEntity<List<DailyTransactionStats>> getAccountDailyTransactionStats(
            @PathVariable String accountNum,
            @PathVariable int month) {
        LocalDateTime startDate = LocalDate.now().withMonth(month).withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        List<TransactionDTO> transactions = transactionService.findTransactionsByDateRangeAndAccount(
                startDate, endDate, accountNum);
        Map<Integer, DailyTransactionStats> dailyStats = new HashMap<>();
        for (TransactionDTO transaction : transactions) {
            int day = transaction.getTransactionTime().getDayOfMonth();
            dailyStats.putIfAbsent(day, new DailyTransactionStats(day));
            DailyTransactionStats stats = dailyStats.get(day);
            BigDecimal amountInByn = currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()),transaction.getCurrency(),"BYN");

            if (transaction.getTransactionType().equals(TransactionType.DEPOSIT) || (transaction.getTransactionType().equals(TransactionType.TRANSFER) && Objects.equals(transaction.getRecipientAccountId(), Long.valueOf(accountNum)))) {
                stats.addDeposit(amountInByn);
            } else if (transaction.getTransactionType().equals(TransactionType.WITHDRAWAL) || (transaction.getTransactionType().equals(TransactionType.TRANSFER) && Objects.equals(transaction.getSenderAccountId(), Long.valueOf(accountNum)))) {
                stats.addWithdrawal(amountInByn);
            }
        }

        List<DailyTransactionStats> response = new ArrayList<>(dailyStats.values());
        response.sort(Comparator.comparingInt(DailyTransactionStats::getDay));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNum}/{month}")
    public ResponseEntity<AccountTransactionStatsDTO> getAccountTransactionStats(
            @PathVariable String accountNum,
            @PathVariable int month) {
        int year = Year.now().getValue();
        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atStartOfDay();

        List<TransactionDTO> transactions = transactionService.findTransactionsByDateRangeAndAccount(
                startDate, endDate, accountNum);

        List<BigDecimal> transactionsInByn = transactions.stream()
                .map(transaction -> currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(),"BYN"))
                .toList();

        BigDecimal maxTransaction = transactionsInByn.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal minTransaction = transactionsInByn.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal avgTransaction = transactionsInByn.isEmpty() ? BigDecimal.ZERO :
                transactionsInByn.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(transactionsInByn.size()), BigDecimal.ROUND_HALF_UP);

        BigDecimal totalDepositInByn = transactionService.findDepositTransactions(accountNum, startDate, endDate)
                .stream()
                .map(transaction -> currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(),"BYN"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawalInByn = transactionService.findWithdrawalTransactions(accountNum, startDate, endDate)
                .stream()
                .map(transaction -> currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(),"BYN"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        AccountTransactionStatsDTO stats = new AccountTransactionStatsDTO(
                maxTransaction, minTransaction, avgTransaction, totalDepositInByn, totalWithdrawalInByn
        );

        return ResponseEntity.ok(stats);
    }
}
