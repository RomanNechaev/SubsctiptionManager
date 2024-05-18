package ru.matmex.subscription.services.impl.export;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.services.ExportReportService;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.impl.export.reports.Report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Создание отчета в формате pdf
 */
@Service
public class PDFService implements ExportReportService {
    /**
     * Размер междустрочного пробела
     */
    private static final float LEADING = 14.5f;
    /**
     * Первоначальное положение текста по X и Y
     */
    private static final int LINEAR_OFFSET_X = 25;
    private static final int LINEAR_OFFSET_Y = 700;
    /**
     * Размер шрифта
     */
    private static final int FONT_SIZE = 12;

    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(PDFService.class);

    @Autowired
    public PDFService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public byte[] loadReport(String nameReport) {
        String userName = userService.getCurrentUser().getUsername();
        UserModel user = userService.getUserModel(userName);
        return reportToPDF(Report.valueOf(nameReport).calculate(user));
    }

    /**
     * Формирование отчета в pdf формате
     *
     * @param report отчет(формат: категория -> стоимость всех подписок)
     * @return массив байт, содержащий отчет
     */
    private byte[] reportToPDF(Map<String, Double> report) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             PDDocument doc = new PDDocument()) {
            PDPage myPage = new PDPage();
            doc.addPage(myPage);
            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {
                cont.beginText();
                cont.setFont(PDType1Font.TIMES_ROMAN, FONT_SIZE);
                cont.setLeading(LEADING);
                cont.newLineAtOffset(LINEAR_OFFSET_X, LINEAR_OFFSET_Y);
                for (String key : report.keySet()) {
                    cont.showText(key + " : " + report.get(key));
                    cont.newLine();
                }
                cont.endText();
            }
            doc.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error(String.format("Не удалось создать .pdf файл для экспорта отчетов у %s",
                    userService.getCurrentUser()));
            throw new RuntimeException(String.format("Не удалось создать .pdf файл для экспорта отчетов у %s",
                    userService.getCurrentUser()));
        }
    }
}
