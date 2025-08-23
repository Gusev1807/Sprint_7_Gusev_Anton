import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private CourierClient courierClient;
    private Integer courierId;

    @Before
    @Step("Подготовка теста")
    public void setup() {
        courierClient = new CourierClient();
        courierId = null;
    }

    @After
    @Step("Очистка после теста")
    public void cleanup() {
        if (courierId != null) {
            deleteCourierById(courierId);
        }
    }

    @Step("Удаление курьера с ID: {id}")
    private void deleteCourierById(Integer id) {
        courierClient.deleteCourier(id)
                .statusCode(200);
    }

    @Test
    @Description("Курьера можно создать")
    public void createCourierSuccessfully() {
        String login = "Gusev" + System.currentTimeMillis();
        Courier courier = new Courier(login, "1234", "Anton");

        createCourierStep(courier);

        ValidatableResponse loginResponse = loginCourierStep(courier);
        courierId = loginResponse.extract().path("id");
    }

    @Step("Создание курьера с логином: {courier.login}")
    private void createCourierStep(Courier courier) {
        courierClient.createCourier(courier)
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Логин курьера с логином: {courier.login}")
    private ValidatableResponse loginCourierStep(Courier courier) {
        return courierClient.loginCourier(
                new Courier(courier.getLogin(), courier.getPassword())
        );
    }

    @Test
    @Description("Нельзя создать двух одинаковых курьеров")
    public void createDuplicateCourier() {
        String login = "duplicateUser" + System.currentTimeMillis();
        Courier courier = new Courier(login, "1234", "test");

        createCourierStep(courier);

        courierId = loginCourierStep(courier).extract().path("id");

        createDuplicateCourierStep(courier);
    }

    @Step("Попытка создать дублирующего курьера с логином: {courier.login}")
    private void createDuplicateCourierStep(Courier courier) {
        courierClient.createCourier(courier)
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @Description("Запрос без логина возвращает 400 и сообщение об ошибке")
    public void createCourierWithoutLogin() {
        Courier courier = new Courier(null, "1234", "saske");
        createCourierExpectingError(courier, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Description("Запрос без пароля возвращает 400 и сообщение об ошибке")
    public void createCourierWithoutPassword() {
        Courier courier = new Courier("noPass" + System.currentTimeMillis(), null, "saske");
        createCourierExpectingError(courier, 400, "Недостаточно данных для создания учетной записи");
    }

    @Step("Создание курьера с ожиданием ошибки: {expectedStatusCode}, сообщение: {expectedMessage}")
    private void createCourierExpectingError(Courier courier, int expectedStatusCode, String expectedMessage) {
        courierClient.createCourier(courier)
                .statusCode(expectedStatusCode)
                .body("message", equalTo(expectedMessage));
    }
}


