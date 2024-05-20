package ru.matmex.subscription.services.impl.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
import java.io.PrintWriter;
import java.util.Map;

/**
 * Создание отчета в формате CSV
 */
@Service
public class CSVService implements ExportReportService {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(CSVService.class);

    @Autowired
    public CSVService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public byte[] loadReport(String nameReport) {
        String userName = userService.getCurrentUser().getUsername();
        UserModel user = userService.getUserModel(userName);
        return reportToCSV(Report.valueOf(nameReport).calculate(user));
    }

    /**
     * Формирование о отчета в формате SCV
     *
     * @param report отчет(формат: категория -> стоимость всех подписок)
     * @return массив байт, содержащий отчет
     */
    private byte[] reportToCSV(Map<String, Double> report) {
        CSVFormat format = CSVFormat
                .Builder
                .create()
                .setDelimiter(';')
                .build();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (String nameReport : report.keySet()) {
                csvPrinter.printRecord(nameReport, report.get(nameReport));
            }
            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException e) {

            logger.error(String.format("Не удалось создать .csv файл для экспорта отчетов у %s",
                    userService.getCurrentUser()));
            throw new RuntimeException(String.format("Не удалось создать .csv файл для экспорта отчетов у %s",
                    userService.getCurrentUser()));
        }
    }
}
