import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import java.util.HashMap;
import java.util.Map;

public class OrderClient extends BaseClient {
    private static final String BASE_PATH = "/api/v1/orders";

    @Step("Создать заказ с параметрами: {order}")
    public ValidatableResponse createOrder(Order order) {
        return reqSpec
                .body(order)
                .when()
                .post(BASE_PATH)
                .then();
    }

    @Step("Принять заказ: orderId={orderId}, courierId={courierId}")
    public ValidatableResponse acceptOrder(Integer orderId, Integer courierId) {
        String url = BASE_PATH + (orderId != null ? "/accept/" + orderId : "/accept/");
        return reqSpec
                .queryParam("courierId", courierId)
                .when()
                .put(url)
                .then();
    }

    @Step("Получить заказ по трек-номеру: track={track}")
    public ValidatableResponse getOrderByTrack(Integer track) {
        return reqSpec
                .queryParam("t", track)
                .when()
                .get(BASE_PATH + "/track")
                .then();
    }

    @Step("Получить список заказов с лимитом {limit} и страницей {page}")
    public ValidatableResponse getOrders(int limit, int page) {
        return reqSpec
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get(BASE_PATH)
                .then();
    }

    @Step("Получить ID первого доступного заказа")
    public int getFirstAvailableOrderId() {
        return getOrders(10, 0)
                .extract()
                .path("orders[0].id");
    }

    @Step("Отменить заказ с трекингом: {track}")
    public ValidatableResponse cancelOrder(int track) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("track", track);

        return reqSpec
                .body(requestBody)
                .when()
                .put(BASE_PATH + "/cancel")
                .then();
    }
}


