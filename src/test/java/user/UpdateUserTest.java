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
import params.UpdateUser;
import steps.BasePage;
import steps.UserApi;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest extends BasePage {
    private String email;
    private String name;
    private String password;
    private String accessToken;
    private CreateUser user;

    @Before
    public void setUp() {
        baseURL();
        user = CreateUser.makeRandomUser();
        Response response = UserApi.createUser(user);
        email = user.getEmail();
        name = user.getName();
        password = user.getPassword();
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Изменение данных пользователя")
    public void testUpdateUser() {
        UpdateUser updateUser = new UpdateUser(RandomStringUtils.randomAlphabetic(8) + "@yandex.ru", RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(7));
        email = updateUser.getEmail();
        name = updateUser.getName();
        password = updateUser.getPassword();
        Response response = UserApi.updateUser(updateUser, accessToken);
        response
                .then().assertThat().statusCode(SC_OK).body("success", equalTo(true)).body("user.email", is(email.toLowerCase())).body("user.name", is(name));
        LoginUser loginUser = new LoginUser(email, password);
        UserApi.loginUser(loginUser).then().assertThat().statusCode(SC_OK);

    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void testUpdateUserWithoutToken() {
        UpdateUser updateUser = new UpdateUser(RandomStringUtils.randomAlphabetic(8) + "@yandex.ru", RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(7));
        Response response = UserApi.updateUser(updateUser);
        response
                .then().assertThat().statusCode(SC_UNAUTHORIZED).body("success", equalTo(false));
    }

    @After
    public void deleteUser() {
        UserApi.deleteUser(new DeleteUser(email, name), accessToken).then().statusCode(SC_ACCEPTED);
    }
}
