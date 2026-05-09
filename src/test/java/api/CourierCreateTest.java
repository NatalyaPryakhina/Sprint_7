package api;

import client.CourierClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.Courier;
import java.util.Map;
import static org.hamcrest.Matchers.is;

public class CourierCreateTest {
    private CourierClient courierClient;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        courierClient = new CourierClient(); // Инициализация клиента
    }

    @Test
    @DisplayName("Создание курьера: успешное создание")
    public void courierCanBeCreated() {
        Courier courier = new Courier("ninja_unique_" + System.currentTimeMillis(), "1234", "Naruto");

        courierClient.create(courier)
                .assertThat()
                .statusCode(201)
                .body("ok", is(true));

        // Получаем ID через логин, чтобы удалить курьера в @After
        courierId = courierClient.login(courier.getLogin(), courier.getPassword())
                .extract().path("id");
    }

    @Test
    @DisplayName("Создание курьера: нельзя создать двух одинаковых")
    public void cannotCreateTwoIdenticalCouriers() {
        String login = "duplicate_ninja_" + System.currentTimeMillis();
        Courier courier = new Courier(login, "1234", "Saske");

        courierClient.create(courier); // Создаем первого

        // Пытаемся создать второго и проверяем ошибку
        courierClient.create(courier)
                .assertThat()
                .statusCode(409)
                .body("message", is("Этот логин уже используется. Попробуйте другой."));

        // Получаем ID первого для очистки
        courierId = courierClient.login(courier.getLogin(), courier.getPassword())
                .extract().path("id");
    }

    @Test
    @DisplayName("Создание курьера: ошибка если нет логина")
    public void createCourierWithoutLoginFails() {
        // Передаем Map без логина
        Map<String, String> body = Map.of("password", "1234", "firstName", "Sakura");

        RestAssured.given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера: ошибка если нет пароля")
    public void createCourierWithoutPasswordFails() {
        Map<String, String> body = Map.of("login", "ninja_" + System.currentTimeMillis(), "firstName", "Sakura");

        RestAssured.given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }
}

