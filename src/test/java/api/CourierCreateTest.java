package api;

import client.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.Courier;
import org.apache.http.HttpStatus;
import static org.hamcrest.Matchers.is;

public class CourierCreateTest extends BaseTest {
    private CourierClient courierClient;
    private Courier courierForCleanup;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("Создание курьера: успешное создание")
    @Description("Проверка возможности успешной регистрации курьера с валидными и уникальными параметрами")
    public void courierCanBeCreatedTest() {
        Courier courier = new Courier("ninja_unique_" + System.currentTimeMillis(), "1234", "Naruto");
        courierForCleanup = courier;

        courierClient.create(courier)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Создание курьера: нельзя создать двух одинаковых")
    @Description("Проверка возврата ошибки 409 (Conflict) при попытке повторной регистрации курьера с уже существующим логином")
    public void cannotCreateTwoIdenticalCouriersTest() {
        String login = "duplicate_ninja_" + System.currentTimeMillis();
        Courier courier = new Courier(login, "1234", "Saske");
        courierForCleanup = courier;

        courierClient.create(courier);

        courierClient.create(courier)
                .assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("message", is("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера: ошибка если нет логина")
    @Description("Негативный тест: проверка возврата ошибки 400 (Bad Request) при создании курьера без указания обязательного поля login")
    public void createCourierWithoutLoginFailsTest() {
        Courier courierWithoutLogin = new Courier(null, "1234", "Sakura");

        courierClient.create(courierWithoutLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера: ошибка если нет пароля")
    @Description("Негативный тест: проверка возврата ошибки 400 (Bad Request) при создании курьера без указания обязательного поля password")
    public void createCourierWithoutPasswordFailsTest() {
        Courier courierWithoutPassword = new Courier("ninja_" + System.currentTimeMillis(), null, "Sakura");

        courierClient.create(courierWithoutPassword)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        if (courierForCleanup != null && courierForCleanup.getLogin() != null && courierForCleanup.getPassword() != null) {
            try {
                var response = courierClient.login(courierForCleanup.getLogin(), courierForCleanup.getPassword());
                if (response.extract().statusCode() == HttpStatus.SC_OK) {
                    int courierId = response.extract().path("id");
                    if (courierId != 0) {
                        courierClient.delete(courierId);
                    }
                }
            } catch (Exception e) {

            }
        }
    }
}
