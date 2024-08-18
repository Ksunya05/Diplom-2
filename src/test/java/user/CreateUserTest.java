package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import params.CreateUser;
import params.DeleteUser;
import steps.BasePage;
import steps.UserApi;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest extends BasePage {
    private String email;
    private String name;
    private String accessToken;
    private CreateUser user;

    @Before
    public void setUp() {
        baseURL();
        user = CreateUser.makeRandomUser();
        email = user.getEmail();
        name = user.getName();

    }

    @Test
    @DisplayName("Создание нового пользователя")
    public void testCreateNewUser() {
        Response response = UserApi.createUser(user);
        response
                .then().assertThat().statusCode(SC_OK).body("success", equalTo(true));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void testCreateTwiceUser() {
        Response response = UserApi.createUser(user);
        Response responseOne = UserApi.createUser(user);
        responseOne
                .then().assertThat().statusCode(SC_FORBIDDEN).body("message", is("User already exists"));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля")
    public void testCreateUserWithoutData() {
        CreateUser createUser = new CreateUser(RandomStringUtils.randomAlphabetic(8) + "@yandex.ru".toLowerCase(), null, RandomStringUtils.randomAlphabetic(7));
        Response response = UserApi.createUser(createUser);
        response
                .then().assertThat().statusCode(SC_FORBIDDEN).body("message", is("Email, password and name are required fields"));
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
