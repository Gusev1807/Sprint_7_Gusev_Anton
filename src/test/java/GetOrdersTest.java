import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    private OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получение списка заказов: успешный возврат списка с пагинацией")
    @Description("Проверка возврата списка заказа")
    public void checkOrdersListIsReturned() {
        orderClient.getOrders(30, 0)
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders", instanceOf(List.class))
                .body("orders.size()", greaterThanOrEqualTo(0));
    }
}
