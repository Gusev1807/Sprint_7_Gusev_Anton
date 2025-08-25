import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.qameta.allure.Description;
import io.qameta.allure.Step;


import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final String[] color;
    private final String colorDescription;
    private final OrderClient orderClient = new OrderClient();
    private Integer trackId;  // Для хранения трек-номера созданного заказа

    public CreateOrderTest(String[] color, String colorDescription) {
        this.color = color;
        this.colorDescription = colorDescription;
    }

    @Parameterized.Parameters(name = "Цвет самоката: {1}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {new String[]{"BLACK"}, "Черный"},
                {new String[]{"GREY"}, "Серый"},
                {new String[]{"BLACK", "GREY"}, "Черный и серый"},
                {new String[]{}, "Без цвета"}
        };
    }

    @Test
    @DisplayName("Создание заказа: проверка разных вариантов цвета самоката")
    @Description("Проверка создания заказа с разными вариантами поля color")
    public void createOrderWithDifferentColors() {
        Order order = buildOrder(color);
        ValidatableResponse response = orderClient.createOrder(order);
        checkOrderCreated(response);

        // Сохраняем трек-номер для отмены в @After
        trackId = response.extract().path("track");
    }

    @After
    @Step("Отмена созданного заказа с треком: {trackId}")
    public void cancelCreatedOrder() {
        if (trackId != null) {
            try {
                orderClient.cancelOrder(trackId);
            } catch (Exception e) {
                System.out.println("Не удалось отменить заказ с треком: " + trackId + ", ошибка: " + e.getMessage());
            }
        }
    }

    @Step("Собираем заказ с цветами: {color}")
    private Order buildOrder(String[] color) {
        return new Order("Anton", "Gusev", "Barcelona, 177 apt.", "4", "+7 937 666 77 88", 4, "2020-06-06", "Anton come back to Barcelona", color);
    }

    @Step("Проверяем, что заказ успешно создан")
    private void checkOrderCreated(ValidatableResponse response) {
        response.statusCode(SC_CREATED)
                .body("track", notNullValue());
    }
}
