import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";

    @Step("Запрос списка заказов без параметров")
    public ValidatableResponse getOrders() {
        return io.restassured.RestAssured
                .given()
                .when()
                .get(BASE_URL)
                .then();
    }

    @Test
    @Description("Проверка возврата списка заказа")
    public void checkOrdersListIsReturned() {
        getOrders()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0))
                .body("orders[0].id", notNullValue())
                .body("orders[0].firstName", notNullValue())
                .body("orders[0].address", notNullValue())
                .body("orders[0].track", notNullValue());
    }
}
