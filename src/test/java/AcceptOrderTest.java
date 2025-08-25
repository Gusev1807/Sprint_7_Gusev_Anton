import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class AcceptOrderTest {

    private CourierClient courierClient;
    private OrderClient orderClient;
    private Integer courierId;
    private Integer orderId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        orderClient = new OrderClient();
        courierId = null;
        orderId = null;

        Courier courier = new Courier("testLogin" + System.currentTimeMillis(), "1234", "TestCourier");
        ValidatableResponse createResponse = createCourier(courier);
        courierId = loginCourier(courier);
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId)
                    .statusCode(SC_OK);
        }
    }

    @Test
    @DisplayName("Принятие заказа: успешный запрос с валидными ID курьера и заказа")
    @Description("Успешный запрос")
    public void acceptOrderSuccessfully() {
        orderId = getFirstAvailableOrderId();

        orderClient.acceptOrder(orderId, courierId)
                .statusCode(SC_OK)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Принятие заказа: ошибка при отсутствии ID курьера")
    @Description("Запрос без id курьера")
    public void acceptOrderWithoutCourierId() {
        orderId = getFirstAvailableOrderId();

        orderClient.acceptOrder(orderId, null)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа: ошибка при несуществующем ID курьера")
    @Description("Запрос с неправильным id курьера")
    public void acceptOrderWithWrongCourierId() {
        orderId = getFirstAvailableOrderId();

        orderClient.acceptOrder(orderId, 999999)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принятие заказа: ошибка при отсутствии ID заказа")
    @Description("Запрос без id заказа")
    public void acceptOrderWithoutOrderId() {
        orderClient.acceptOrder(null, courierId)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа: ошибка при несуществующем ID заказа")
    @Description("Запрос с неправильным id заказа")
    public void acceptOrderWithWrongOrderId() {
        orderClient.acceptOrder(999999, courierId)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Step("Создать курьера")
    private ValidatableResponse createCourier(Courier courier) {
        return courierClient.createCourier(courier).statusCode(SC_CREATED);
    }

    @Step("Логин курьера")
    private Integer loginCourier(Courier courier) {
        return courierClient.loginCourier(new Courier(courier.getLogin(), courier.getPassword()))
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }

    @Step("Получить id первого доступного заказа")
    private Integer getFirstAvailableOrderId() {
        return orderClient.getFirstAvailableOrderId();
    }
}

