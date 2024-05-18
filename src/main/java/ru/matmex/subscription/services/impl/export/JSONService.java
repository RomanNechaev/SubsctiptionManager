package ru.matmex.subscription.services.impl.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.services.ExportReportService;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.impl.export.reports.Report;

/**
 * Создание отчета в формате JSON
 */
@Service
public class JSONService implements ExportReportService {
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(JSONService.class);

    @Autowired
    public JSONService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Загрузить отчет в формате JSON
     *
     * @param nameReport -  название отчета
     * @return массив байт, содержащий отчет
     */
    public byte[] loadReport(String nameReport) {
        String userName = userService.getCurrentUser().getUsername();
        UserModel user = userService.getUserModel(userName);
        try {
            return new ObjectMapper().writeValueAsBytes(Report.valueOf(nameReport).calculate(user));
        } catch (JsonProcessingException e) {
            logger.error(String.format("Не удалось создать .json файл для экспорта отчетов у %s",
                    userService.getCurrentUser()));
            throw new RuntimeException(String.format("Не удалось создать .json файл для экспорта отчетов у %s",
                    userService.getCurrentUser()));
        }
    }
}
