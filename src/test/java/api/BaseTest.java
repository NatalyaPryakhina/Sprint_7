package api;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class BaseTest {

    @BeforeClass
    public static void globalSetUp() {

        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }
}
