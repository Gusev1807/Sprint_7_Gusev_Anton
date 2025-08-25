import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class DeleteCourierTest {

    private CourierClient courierClient;
    private Integer courierId;

    @Before
    public void setup() {
        courierClient = new CourierClient();
        courierId = null;
    }

    @After
    public void cleanup() {
        if (courierId != null) {
            deleteCourierSafely(courierId);
        }
    }

    @Test
    @DisplayName("Удаление курьера: успешное удаление по ID")
    @Description("Успешное удаление курьера")
    public void deleteCourierSuccessfully() {
        courierId = createCourierForTest();

        deleteCourier(courierId)
                .statusCode(SC_OK)
                .body("ok", equalTo(true));


        loginCourierExpectingError(courierId);
        courierId = null; // уже удалён
    }

    @Test
    @DisplayName("Удаление курьера: ошибка при отсутствии ID")
    @Description("Попытка удалить курьера без ID возвращает ошибку")
    public void deleteCourierWithoutId() {
        givenDeleteCourier(null)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Удаление курьера: ошибка при несуществующем ID")
    @Description("Попытка удалить несуществующего курьера возвращает ошибку")
    public void deleteNonExistentCourier() {
        int fakeId = 999999;

        deleteCourier(fakeId)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id нет."));
    }

    @Step("Создать курьера для теста")
    private Integer createCourierForTest() {
        Courier courier = new Courier("testUser" + System.currentTimeMillis(), "1234", "Test");
        courierClient.createCourier(courier).statusCode(SC_CREATED);

        return courierClient.loginCourier(new Courier(courier.getLogin(), courier.getPassword()))
                .extract().path("id");
    }

    @Step("Удалить курьера с id {courierId}")
    private ValidatableResponse deleteCourier(int courierId) {
        return courierClient.deleteCourier(courierId);
    }

    @Step("Попытка безопасного удаления курьера, чтобы избежать ошибок после теста")
    private void deleteCourierSafely(int courierId) {
        try {
            deleteCourier(courierId).statusCode(SC_OK);
        } catch (Exception ignored) {}
    }

    @Step("Попытка войти под удалённым курьером с id {courierId}")
    private void loginCourierExpectingError(int courierId) {
        courierClient.loginCourier(new Courier("fake", "fake"))
                .statusCode(SC_NOT_FOUND);
    }

    @Step("Отправить DELETE без id")
    private ValidatableResponse givenDeleteCourier(Object id) {
        return courierClient.deleteCourier(id == null ? 0 : (Integer) id);
    }
}
