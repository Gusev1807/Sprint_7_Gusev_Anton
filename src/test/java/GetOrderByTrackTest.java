import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

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
    @Description("Успешное получение заказа по трек-номеру")
    public void getOrderByTrackSuccessfully() {
        orderClient.getOrderByTrack(orderTrack)
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.track", equalTo(orderTrack));
    }

    @Test
    @Description("Попытка получить заказ без трек-номера возвращает ошибку")
    public void getOrderWithoutTrack() {
        orderClient.getOrderByTrack(null)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @Description("Попытка получить несуществующий заказ возвращает ошибку")
    public void getNonExistentOrder() {
        orderClient.getOrderByTrack(999999)
                .statusCode(404)
                .body("message", equalTo("Заказ не найден"));
    }
}

