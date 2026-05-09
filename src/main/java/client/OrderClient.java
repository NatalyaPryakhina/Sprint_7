package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDERS_PATH = "/api/v1/orders";

    @Step("Создание заказа")
    public ValidatableResponse create(Object orderJson) {
        return given()
                .header("Content-type", "application/json")
                .body(orderJson)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getList() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(ORDERS_PATH)
                .then();
    }
}
