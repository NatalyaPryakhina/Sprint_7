package api;

import client.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.Courier;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest extends BaseTest {
    private CourierClient courierClient;
    private Courier courierForTest;

    @Before
    public void setUp() {
        courierClient = new CourierClient();

        String login = "ninja_login_" + System.currentTimeMillis();
        courierForTest = new Courier(login, "password123", "Naruto");
        courierClient.create(courierForTest);
    }

    @Test
    @DisplayName("Логин курьера: успех возвращает id")
    @Description("Проверка успешной авторизации курьера и получения уникального ID пользователя в ответе")
    public void courierCanLoginTest() {
        courierClient.login(courierForTest.getLogin(), courierForTest.getPassword())
                .assertThat()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин курьера: ошибка при неправильном пароле")
    @Description("Негативный тест: проверка возврата ошибки 404 (Not Found) при попытке авторизоваться с некорректным паролем")
    public void loginWithWrongPasswordFailsTest() {
        courierClient.login(courierForTest.getLogin(), "WRONG_PASSWORD")
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера: ошибка при отсутствии логина")
    @Description("Негативный тест: проверка возврата ошибки 400 (Bad Request) при попытке авторизации без указания логина")
    public void loginWithoutLoginFieldFailsTest() {
        courierClient.login(null, "12345")
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера: ошибка при отсутствии пароля")
    @Description("Негативный тест: проверка возврата ошибки 400 (Bad Request) при попытке авторизации без указания пароля")
    public void loginWithoutPasswordFieldFailsTest() {
        courierClient.login(courierForTest.getLogin(), null)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера: ошибка для несуществующего пользователя")
    @Description("Негативный тест: проверка возврата ошибки 404 (Not Found) при авторизации под вымышленными учетными данными")
    public void loginNonExistentUserFailsTest() {
        courierClient.login("non_existent_ninja_999", "6666")
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        if (courierForTest != null) {
            try {
                var response = courierClient.login(courierForTest.getLogin(), courierForTest.getPassword());
                if (response.extract().statusCode() == SC_OK) {
                    int id = response.extract().path("id");
                    if (id != 0) {
                        courierClient.delete(id);
                    }
                }
            } catch (Exception e) {

            }
        }
    }
}
