import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
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

        // создаем курьера и сохраняем его id
        Courier courier = new Courier("testLogin" + System.currentTimeMillis(), "1234", "TestCourier");
        ValidatableResponse createResponse = createCourier(courier);
        courierId = loginCourier(courier);
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId)
                    .statusCode(200);
        }
    }

    @Test
    @Description("Успешный запрос")
    public void acceptOrderSuccessfully() {
        orderId = getFirstAvailableOrderId();

        given()
                .queryParam("courierId", courierId)
                .when()
                .put("/api/v1/orders/accept/" + orderId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Description("Запрос без id курьера")
    public void acceptOrderWithoutCourierId() {
        orderId = getFirstAvailableOrderId();

        given()
                .when()
                .put("/api/v1/orders/accept/" + orderId)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @Description("Запрос с неправильным id курьера")
    public void acceptOrderWithWrongCourierId() {
        orderId = getFirstAvailableOrderId();

        given()
                .queryParam("courierId", 999999)
                .when()
                .put("/api/v1/orders/accept/" + orderId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @Description("Запрос без id заказа")
    public void acceptOrderWithoutOrderId() {
        given()
                .queryParam("courierId", courierId)
                .when()
                .put("/api/v1/orders/accept/")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @Description("Запрос с неправильным id заказа")
    public void acceptOrderWithWrongOrderId() {
        given()
                .queryParam("courierId", courierId)
                .when()
                .put("/api/v1/orders/accept/999999")
                .then()
                .statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Step("Создать курьера")
    private ValidatableResponse createCourier(Courier courier) {
        return courierClient.createCourier(courier).statusCode(201);
    }

    @Step("Логин курьера")
    private Integer loginCourier(Courier courier) {
        return courierClient.loginCourier(new Courier(courier.getLogin(), courier.getPassword()))
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Step("Получить id первого доступного заказа")
    private Integer getFirstAvailableOrderId() {
        return orderClient.getOrders(10, 0)
                .statusCode(200)
                .extract()
                .path("orders[0].id");
    }
}

