import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {
    protected RequestSpecification reqSpec;

    public BaseClient() {
        reqSpec = given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .contentType(ContentType.JSON);
    }
}
