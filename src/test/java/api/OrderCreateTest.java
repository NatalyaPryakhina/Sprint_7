package api;

import client.OrderClient;
import model.Order;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.List;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest extends BaseTest {

    private final List<String> color;
    private OrderClient orderClient;
    private int orderTrack;

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

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
    @Description("Параметризованный тест: проверка успешного создания заказа в Самокате с выбором одного цвета, нескольких цветов или без указания цвета вообще")
    public void createOrderWithDifferentColorsTest() {
        Order order = new Order(
                "Naruto",
                "Uzumaki",
                "Konoha, 142",
                "4",
                "+7 800 355 35 35",
                5,
                "2026-07-07",
                "Saske, come back!",
                color
        );

        orderTrack = orderClient.create(order)
                .assertThat()
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .extract()
                .path("track");
    }

    @After
    public void tearDown() {
        if (orderTrack != 0) {
            try {
                orderClient.cancel(orderTrack);
            } catch (Exception e) {

            }
        }
    }
}
