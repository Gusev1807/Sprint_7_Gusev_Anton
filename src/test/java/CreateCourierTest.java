import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private CourierClient courierClient;
    private Integer courierId;
    private Courier testCourier;  // ← Новое поле

    @Before
    public void setup() {
        courierClient = new CourierClient();
        courierId = null;
        testCourier = null;
    }

    @After
    @Step("Очистка после теста: попытка удаления созданного курьера")
    public void cleanup() {
        if (testCourier != null) {
            try {
                // Пытаемся залогинить и удалить даже если тест упал
                ValidatableResponse loginResponse = loginCourierStep(testCourier);
                courierId = loginResponse.extract().path("id");

                if (courierId != null) {
                    deleteCourierById(courierId);
                }
            } catch (Exception e) {
                // Игнорируем ошибки в after-методе
                System.out.println("Ошибка при очистке: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Создание курьера: успешное создание с валидными данными")
    @Description("Курьера можно создать")
    public void createCourierSuccessfully() {
        String login = "Gusev" + System.currentTimeMillis();
        testCourier = new Courier(login, "1234", "Anton");

        createCourierStep(testCourier);
        // Логин перенесен в @After
    }

    @Test
    @DisplayName("Создание курьера: ошибка при дублировании логина")
    @Description("Нельзя создать двух одинаковых курьеров")
    public void createDuplicateCourier() {
        String login = "duplicateUser" + System.currentTimeMillis();
        testCourier = new Courier(login, "1234", "test");

        createCourierStep(testCourier);

        // Первый логин для получения ID
        ValidatableResponse loginResponse = loginCourierStep(testCourier);
        courierId = loginResponse.extract().path("id");

        createDuplicateCourierStep(testCourier);
    }

    @Test
    @DisplayName("Создание курьера: ошибка при отсутствии логина")
    @Description("Запрос без логина возвращает 400 и сообщение об ошибке")
    public void createCourierWithoutLogin() {
        testCourier = new Courier(null, "1234", "saske");
        createCourierExpectingError(testCourier, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Создание курьера: ошибка при отсутствии пароля")
    @Description("Запрос без пароля возвращает 400 и сообщение об ошибке")
    public void createCourierWithoutPassword() {
        testCourier = new Courier("noPass" + System.currentTimeMillis(), "", "saske");
        createCourierExpectingError(testCourier, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи");
    }

    // Остальные методы без изменений...
    @Step("Создание курьера с логином: {courier.login}")
    private void createCourierStep(Courier courier) {
        courierClient.createCourier(courier)
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Step("Логин курьера с логином: {courier.login}")
    private ValidatableResponse loginCourierStep(Courier courier) {
        return courierClient.loginCourier(
                new Courier(courier.getLogin(), courier.getPassword())
        ).statusCode(SC_OK);
    }

    @Step("Попытка создать дублирующего курьера с логином: {courier.login}")
    private void createDuplicateCourierStep(Courier courier) {
        courierClient.createCourier(courier)
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Step("Создание курьера с ожиданием ошибки: {expectedStatusCode}, сообщение: {expectedMessage}")
    private void createCourierExpectingError(Courier courier, int expectedStatusCode, String expectedMessage) {
        courierClient.createCourier(courier)
                .statusCode(expectedStatusCode)
                .body("message", equalTo(expectedMessage));
    }

    @Step("Удаление курьера с ID: {id}")
    private void deleteCourierById(Integer id) {
        courierClient.deleteCourier(id)
                .statusCode(SC_OK);
    }
}


