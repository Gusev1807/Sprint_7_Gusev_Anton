import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final String[] color;
    private final OrderClient orderClient = new OrderClient();

    public CreateOrderTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColorData() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        };
    }

    @Test
    @Description("Проверка создания заказа с разными вариантами поля color")
    public void createOrderWithDifferentColors() {
        Order order = buildOrder(color);
        ValidatableResponse response = orderClient.createOrder(order);
        checkOrderCreated(response);
    }

    @Step("Собираем заказ с цветами: {color}")
    private Order buildOrder(String[] color) {
        return new Order("Anton", "Gusev", "Barcelona, 177 apt.", "4", "+7 937 666 77 88", 4, "2020-06-06", "Anton come back to Barcelona", color);
    }

    @Step("Проверяем, что заказ успешно создан")
    private void checkOrderCreated(ValidatableResponse response) {
        response.statusCode(201)
                .body("track", notNullValue());
    }

}
