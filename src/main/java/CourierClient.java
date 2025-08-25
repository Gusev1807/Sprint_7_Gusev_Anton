import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.Map;

public class CourierClient extends BaseClient {
    private static final String BASE_PATH = "/api/v1/courier";

    @Step("Создание курьера с логином: {courier.login}")
    public ValidatableResponse createCourier(Courier courier) {
        return reqSpec
                .body(courier)
                .when()
                .post(BASE_PATH)
                .then();
    }

    @Step("Логин курьера с логином: {courier.login}")
    public ValidatableResponse loginCourier(Courier courier) {
        return reqSpec
                .body(courier)
                .when()
                .post(BASE_PATH + "/login")
                .then();
    }

    @Step("Удаление курьера с ID: {courierId}")
    public ValidatableResponse deleteCourier(int courierId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", courierId);

        return reqSpec
                .body(requestBody)
                .when()
                .delete(BASE_PATH + "/" + courierId)
                .then();
    }
}

