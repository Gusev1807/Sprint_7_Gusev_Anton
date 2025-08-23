import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_PATH = "/api/v1/orders";

    public OrderClient() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Step("Создать заказ с параметрами: {order}")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(BASE_PATH)
                .then();
    }

    @Step("Принять заказ: orderId={orderId}, courierId={courierId}")
    public ValidatableResponse acceptOrder(Integer orderId, Integer courierId) {
        String url = BASE_PATH + (orderId != null ? "/accept/" + orderId : "/accept/");
        return given()
                .queryParam("courierId", courierId)
                .when()
                .put(url)
                .then();
    }

    @Step("Получить заказ по трек-номеру: track={track}")
    public ValidatableResponse getOrderByTrack(Integer track) {
        return given()
                .queryParam("t", track)
                .when()
                .get(BASE_PATH + "/track")
                .then();
    }

    @Step("Получить список заказов с лимитом {limit} и страницей {page}")
    public ValidatableResponse getOrders(int limit, int page) {
        return given()
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get(BASE_PATH)
                .then();
    }

    @Step("Отменить заказ с трекингом: {track}")
    public ValidatableResponse cancelOrder(int track) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"track\": " + track + "}")
                .when()
                .put(BASE_PATH + "/cancel")
                .then();
    }
}


