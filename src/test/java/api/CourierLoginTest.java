package api;

import client.CourierClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.Courier;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private CourierClient courierClient;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("Логин курьера: успех возвращает id")
    public void courierCanLogin() {
        String login = "ninja_login_" + System.currentTimeMillis();
        Courier courier = new Courier(login, "password123", "Naruto");
        courierClient.create(courier);

        // Логинимся и сохраняем ID для удаления
        courierId = courierClient.login(courier.getLogin(), courier.getPassword())
                .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().path("id");
    }

    @Test
    @DisplayName("Логин курьера: ошибка при неправильном пароле")
    public void loginWithWrongPasswordFails() {
        String login = "ninja_login_" + System.currentTimeMillis();
        Courier courier = new Courier(login, "password123", "Naruto");
        courierClient.create(courier);

        // Пытаемся зайти с неверным паролем
        courierClient.login(login, "WRONG_PASSWORD")
                .assertThat()
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));

        // Получаем ID для очистки (с верным паролем)
        courierId = courierClient.login(login, "password123").extract().path("id");
    }

    @Test
    @DisplayName("Логин курьера: ошибка при отсутствии логина")
    public void loginWithoutFieldFails() {
        // Прямой вызов без одного поля
        RestAssured.given()
                .header("Content-type", "application/json")
                .body(java.util.Map.of("password", "12345"))
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера: ошибка для несуществующего пользователя")
    public void loginNonExistentUserFails() {
        courierClient.login("non_existent_ninja_999", "6666")
                .assertThat()
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }
}
