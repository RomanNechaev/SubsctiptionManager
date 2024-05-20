package ru.matmex.subscription.services.impl.export;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.matmex.subscription.entities.Category;
import ru.matmex.subscription.entities.Subscription;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.services.ExportReportService;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.utils.mapping.CategoryModelMapper;
import ru.matmex.subscription.utils.CategoryBuilder;
import ru.matmex.subscription.utils.SubscriptionBuilder;
import ru.matmex.subscription.utils.UserBuilder;

import static org.assertj.core.api.Assertions.assertThat;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

/**
 * Тестирование сервисов формирования отчета
 */
class ExportServiceTest {
    private final UserService userService = Mockito.mock(UserService.class);
    private final CategoryModelMapper categoryModelMapper = new CategoryModelMapper();

    private final ExportReportService csvService = new CSVService(userService);
    private final ExportReportService pdfService = new PDFService(userService);
    private final ExportReportService jsonService = new JSONService(userService);

    /**
     * Тестирование загрузки отчета
     */
    @Test
    void testCanLoadReport() throws IOException {
        User testUser = UserBuilder.anUser().build();

        Category category = CategoryBuilder.anCategory().defaultCategory();

        Subscription sub1 = SubscriptionBuilder.anSubscription()
                .withName("test")
                .withUser(testUser)
                .withPrice(100.0)
                .withCategory(category)
                .build();

        Subscription sub2 = SubscriptionBuilder.anSubscription()
                .withName("test2")
                .withUser(testUser)
                .withPrice(200.0)
                .withCategory(category)
                .build();

        category.setSubscriptions(List.of(sub1, sub2));

        UserModel userModel = new UserModel(1L, "test","test@gmail.com",2L, Stream.of(category).map(categoryModelMapper::map).toList());

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(userService.getUserModel(testUser.getUsername())).thenReturn(userModel);

        String reportName = "TotalPriceCategory";

        byte[] actualCSV = csvService.loadReport(reportName);
        byte[] expectedCSV = FileUtils.readFileToByteArray(new File("./src/test/java/ru/matmex/subscription/resources/expectedResult.csv"));

        byte[] actualJSON = jsonService.loadReport(reportName);
        byte[] expectedJSON = FileUtils.readFileToByteArray(new File("./src/test/java/ru/matmex/subscription/resources/expectedResult.json"));

        byte[] actualPDF = pdfService.loadReport(reportName);
        //byte[] expectedPDF = FileUtils.readFileToByteArray(new File("./src/test/java/ru/matmex/subscription/resources/expectedResult.pdf"));

        assertThat(actualCSV).isEqualTo(expectedCSV);
        assertThat(actualJSON).isEqualTo(expectedJSON);
        assertThat(actualPDF).isNotNull();
    }
}