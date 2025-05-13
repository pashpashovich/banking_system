package by.bsuir.bank.service;

import by.bsuir.bank.domain.TransactionDTO;
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
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class PdfReceiptService {

  private static final String FONT_PATH = "src/main/resources/fonts/DejaVuSans.ttf";
  private static final String IMAGE_PATH = "src/main/resources/images/logo.png";

  public byte[] generateReceiptPdf(TransactionDTO transaction) throws Exception {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      PdfWriter writer = new PdfWriter(outputStream);
      PdfDocument pdfDocument = new PdfDocument(writer);
      Document document = new Document(pdfDocument);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
      PdfFont font = PdfFontFactory.createFont(FONT_PATH, PdfEncodings.IDENTITY_H);

      Image logo = new Image(ImageDataFactory.create(IMAGE_PATH)).scaleToFit(30, 30).setFixedPosition(40, 750);
      document.add(logo);

      Paragraph bankName = new Paragraph("FinScope").setFont(font).setFontSize(24).setBold()
          .setFontColor(ColorConstants.BLACK).setFixedPosition(80, 747, 1000);
      document.add(bankName);

      Paragraph title = new Paragraph("Чек").setFont(font).setFontSize(18).setBold()
          .setTextAlignment(TextAlignment.CENTER).setPaddingTop(35).setMarginBottom(30)
          .setFontColor(ColorConstants.BLACK);
      document.add(title);

      Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2})).useAllAvailableWidth()
          .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

      addTableRow(table, "ID транзакции:", String.valueOf(transaction.getId()), font);
      addTableRow(table, "Тип транзакции:", transaction.getTransactionType().getDisplayName(), font);
      addTableRow(table, "Номер счета отправителя:",
          transaction.getSenderAccountId() != null ? String.valueOf(transaction.getSenderAccountId()) : "-", font);

      addTableRow(table, "Номер счета получателя:",
          transaction.getRecipientAccountId() != null ? String.valueOf(transaction.getRecipientAccountId()) : "-",
          font);

      addTableRow(table, "Сумма:", transaction.getAmount() + " " + transaction.getCurrency(), font);
      addTableRow(table, "Дата и время:", transaction.getTransactionTime().format(formatter), font);

      document.add(table);
      document.close();

      return outputStream.toByteArray();
    }
  }

  private void addTableRow(Table table, String label, String value, PdfFont font) {
    table.addCell(new Paragraph(label).setFont(font).setBold());
    table.addCell(new Paragraph(value).setFont(font));
  }

}
