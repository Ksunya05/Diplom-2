package base;

import io.restassured.RestAssured;


public class BaseTest {
    public void baseURL() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

}
