import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class LoginCourierTest {

    private CourierClient courierClient;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courierId = null;
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Step("Создать курьера")
    private void createCourier(Courier courier) {
        courierClient.createCourier(courier)
                .statusCode(201);
    }

    @Step("Авторизовать курьера")
    private ValidatableResponse loginCourier(Courier courier) {
        return courierClient.loginCourier(courier);
    }

    @Step("Удалить курьера")
    private void deleteCourier(int id) {
        courierClient.deleteCourier(id)
                .statusCode(200);
    }

    @Test
    @Description("Курьер может авторизоваться")
    public void loginSuccessfully() {
        Courier courier = new Courier("Gusev" + System.currentTimeMillis(), "1234", "Gusev");

        createCourier(courier);

        ValidatableResponse response = loginCourier(new Courier(courier.getLogin(), courier.getPassword()));

        response.statusCode(200)
                .body("id", notNullValue());

        courierId = response.extract().path("id");
    }

    @Test
    @Description("Запрос без логина возвращает 400 и сообщение об ошибке")
    public void loginWithoutLogin() {
        ValidatableResponse response = loginCourier(new Courier("", "1234"));

        response.statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Запрос без пароля возвращает 400 и сообщение об ошибке")
    public void loginWithoutPassword() {
        ValidatableResponse response = loginCourier(new Courier("Gusev" + System.currentTimeMillis(), ""));

        response.statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Авторизация с неверным логином или паролем возвращает 404")
    public void loginWithWrongCredentials() {
        ValidatableResponse response = loginCourier(new Courier("wrongUser", "wrongPass"));

        response.statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Авторизация несуществующего пользователя возвращает 404")
    public void loginNonExistentUser() {
        ValidatableResponse response = loginCourier(new Courier("nonExistent", "1234"));

        response.statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}


