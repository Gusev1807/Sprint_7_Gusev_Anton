import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String BASE_PATH = "/api/v1/courier";

    public CourierClient() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Step("Создание курьера с логином: {courier.login}")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(BASE_PATH)
                .then();
    }

    @Step("Логин курьера с логином: {courier.login}")
    public ValidatableResponse loginCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(BASE_PATH + "/login")
                .then();
    }

    @Step("Удаление курьера с ID: {courierId}")
    public ValidatableResponse deleteCourier(int courierId) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"id\": \"" + courierId + "\"}")
                .when()
                .delete(BASE_PATH + "/" + courierId)
                .then();
    }

}

