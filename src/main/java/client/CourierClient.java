package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Courier;

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
        // Используем Map для формирования JSON "на лету"
        var credentials = java.util.Map.of("login", login, "password", password);
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
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