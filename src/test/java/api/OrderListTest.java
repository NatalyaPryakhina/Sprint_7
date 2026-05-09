package api;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

public class OrderListTest extends BaseTest {

    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение списка всех заказов")
    @Description("Проверка получения непустого списка существующих заказов в системе и контроль валидности структуры первого элемента")
    public void getOrderListReturnsOrdersTest() {
        orderClient.getList()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", is(not(empty())))
                .body("orders.id", notNullValue());
    }
}

