package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import params.CreateUser;
import params.DeleteUser;
import params.LoginUser;
import steps.BasePage;
import steps.UserApi;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest extends BasePage {
    private String email;
    private String name;
    private String password;
    private String accessToken;
    private CreateUser user;

    @Before

    public void setUp() {
        baseURL();
        user = CreateUser.makeRandomUser();
        UserApi.createUser(user);
        email = user.getEmail();
        name = user.getName();
        password = user.getPassword();
    }

    @Test
    @DisplayName("Авторизация пользователя")
    public void testLoginUser() {
        LoginUser loginUser = new LoginUser(email, password);
        Response response = UserApi.loginUser(loginUser);
        response
                .then().assertThat().statusCode(SC_OK).body("success", equalTo(true));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным логином и паролем")
    public void testWrongLoginUser() {
        LoginUser loginUser = new LoginUser(RandomStringUtils.randomAlphabetic(8) + "@yandex.ru", RandomStringUtils.randomAlphabetic(5));
        Response response = UserApi.loginUser(loginUser);
        response
                .then().assertThat().statusCode(SC_UNAUTHORIZED).body("success", equalTo(false));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() throws Exception {
        try {
            UserApi.deleteUser(new DeleteUser(email, name), accessToken).then().statusCode(SC_ACCEPTED);
        } catch (Exception e) {
        }
    }
}