package api;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Получение списка всех заказов")
    public void getOrderListReturnsOrders() {
        given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(200)
                // Проверяем, что поле "orders" не пустое и является списком
                .body("orders", is(not(empty())))
                // Можно также проверить, что в списке возвращаются объекты с полями заказа
                .body("orders[0].id", notNullValue());
    }
}
