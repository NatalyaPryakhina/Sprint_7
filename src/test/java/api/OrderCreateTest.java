package api;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final List<String> color;

    // Конструктор принимает набор цветов для текущего теста
    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    // Данные для теста: BLACK, GREY, оба, ни одного
    @Parameterized.Parameters(name = "Тест с цветом: {0}")
    public static Object[][] getOrderData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()}
        };
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цветов")
    public void createOrderWithDifferentColors() {
        // Создаем тело запроса (в реальном проекте лучше использовать POJO класс Order)
        OrderJson body = new OrderJson(
                "Naruto",
                "Uzumaki",
                "Konoha, 142",
                "4",
                "+7 800 355 35 35",
                5,
                "2024-07-07",
                "Saske, come back!",
                color
        );

        given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }

    // Вспомогательный статический класс для тела заказа (вместо отдельного файла)
    static class OrderJson {
        public String firstName;
        public String lastName;
        public String address;
        public String metroStation;
        public String phone;
        public int rentTime;
        public String deliveryDate;
        public String comment;
        public List<String> color;

        public OrderJson(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment, List<String> color) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.metroStation = metroStation;
            this.phone = phone;
            this.rentTime = rentTime;
            this.deliveryDate = deliveryDate;
            this.comment = comment;
            this.color = color;
        }
    }
}

