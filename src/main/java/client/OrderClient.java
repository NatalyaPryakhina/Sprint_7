package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Order;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDERS_PATH = "/api/v1/orders";

    @Step("Создание заказа")
    public ValidatableResponse create(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
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

    @Step("Отмена заказа по трек-номуру {track}")
    public ValidatableResponse cancel(int track) {

        Map<String, Integer> body = Map.of("track", track);

        return given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .put(ORDERS_PATH + "/cancel")
                .then();
    }
}

