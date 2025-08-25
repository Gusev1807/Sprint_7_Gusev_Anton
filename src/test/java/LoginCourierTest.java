import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private CourierClient courierClient;
    private Integer courierId;
    private Courier testCourier;

    @Before
    @Step("Подготовка теста: создание тестового курьера")
    public void setUp() {
        courierClient = new CourierClient();
        courierId = null;
        testCourier = null;
        createTestCourier();
    }

    @After
    @Step("Очистка после теста: удаление тестового курьера")
    public void cleanUp() {
        if (courierId != null) {
            try {
                deleteCourier(courierId);
            } catch (Exception e) {
                System.out.println("Ошибка при удалении курьера: " + e.getMessage());
            }
        }
    }

    @Step("Создание тестового курьера")
    private void createTestCourier() {
        String login = "Gusev" + System.currentTimeMillis();
        testCourier = new Courier(login, "1234", "Gusev");
        createCourier(testCourier);
    }

    @Test
    @DisplayName("Логин курьера: успешная авторизация с валидными данными")
    @Description("Курьер может авторизоваться")
    public void loginSuccessfully() {
        ValidatableResponse response = loginCourier(
                new Courier(testCourier.getLogin(), testCourier.getPassword())
        );

        response.statusCode(SC_OK)
                .body("id", notNullValue());

        courierId = response.extract().path("id");
    }

    @Test
    @DisplayName("Логин курьера: ошибка при отсутствии логина")
    @Description("Запрос без логина возвращает 400 и сообщение об ошибке")
    public void loginWithoutLogin() {
        ValidatableResponse response = loginCourier(new Courier("", "1234"));
        response.statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера: ошибка при отсутствии пароля")
    @Description("Запрос без пароля возвращает 400 и сообщение об ошибке")
    public void loginWithoutPassword() {
        ValidatableResponse response = loginCourier(
                new Courier("testUser" + System.currentTimeMillis(), "")
        );
        response.statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера: ошибка при неверных учетных данных")
    @Description("Авторизация с неверным логином или паролем возвращает 404")
    public void loginWithWrongCredentials() {
        ValidatableResponse response = loginCourier(new Courier("wrongUser", "wrongPass"));
        response.statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера: ошибка при несуществующем пользователе")
    @Description("Авторизация несуществующего пользователя возвращает 404")
    public void loginNonExistentUser() {
        ValidatableResponse response = loginCourier(new Courier("nonExistent", "1234"));
        response.statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Создать курьера")
    private void createCourier(Courier courier) {
        courierClient.createCourier(courier)
                .statusCode(SC_CREATED);
    }

    @Step("Авторизовать курьера")
    private ValidatableResponse loginCourier(Courier courier) {
        return courierClient.loginCourier(courier);
    }

    @Step("Удалить курьера")
    private void deleteCourier(int id) {
        courierClient.deleteCourier(id)
                .statusCode(SC_OK);
    }
}


