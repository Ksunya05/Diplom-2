package steps;

import io.restassured.RestAssured;


public class BasePage {
    public void baseURL() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

}
