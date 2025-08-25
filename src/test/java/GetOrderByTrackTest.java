import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class GetOrderByTrackTest {

    private OrderClient orderClient;
    private Integer orderTrack;

    @Before
    public void setUp() {
        orderClient = new OrderClient();

        // Берем трек первого доступного заказа
        orderTrack = orderClient.getOrders(10, 0).extract().path("orders[0].track");
    }

    @Test
    @DisplayName("Получение заказа: успешный поиск по трек-номеру")
    @Description("Успешное получение заказа по трек-номеру")
    public void getOrderByTrackSuccessfully() {
        orderClient.getOrderByTrack(orderTrack)
                .statusCode(SC_OK)
                .body("order", notNullValue())
                .body("order.track", equalTo(orderTrack));
    }

    @Test
    @DisplayName("Получение заказа: ошибка при отсутствии трек-номера")
    @Description("Попытка получить заказ без трек-номера возвращает ошибку")
    public void getOrderWithoutTrack() {
        orderClient.getOrderByTrack(null)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Получение заказа: ошибка при несуществующем трек-номере")
    @Description("Попытка получить несуществующий заказ возвращает ошибку")
    public void getNonExistentOrder() {
        orderClient.getOrderByTrack(999999)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказ не найден"));
    }
}

