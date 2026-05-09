package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";

    @Step("Создание курьера {courier.login}")
    public ValidatableResponse create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then();
    }

    @Step("Авторизация курьера с логином {login}")
    public ValidatableResponse login(String login, String password) {

        CourierCredentials credentials = new CourierCredentials(login, password);

        return given()
                .header("Content-type", "application/json")
                .body(credentials) // RestAssured автоматически сериализует POJO-объект в JSON
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Удаление курьера по id {courierId}")
    public ValidatableResponse delete(int courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(COURIER_PATH + "/" + courierId)
                .then();
    }
}
