package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import params.CreateUser;
import params.DeleteUser;
import params.LoginUser;
import params.UpdateUser;

import static io.restassured.RestAssured.given;

public class UserApi {
    private static final String hand_create_user = "/api/auth/register";
    private static final String hand_login_user = "/api/auth/login";
    private static final String hand_delete_user = "/api/auth/user";
    private static final String hand_update = "/api/auth/user";

    @Step("Создание пользователя")
    public static Response createUser(CreateUser createUser) {
        return given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(createUser)
                .when()
                .post(hand_create_user);
    }

    @Step("Авторизация пользователя")
    public static Response loginUser(LoginUser loginUser) {
        return given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(loginUser)
                .when()
                .post(hand_login_user);
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(DeleteUser deleteUser, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(deleteUser)
                .when()
                .delete(hand_delete_user);
    }

    @Step("Изменение данных пользователя с токеном")
    public static Response updateUser(UpdateUser updateUser, String accessToken) {
        RequestSpecification requestSpecification =
                given().log().all()
                        .header("Content-type", "application/json");
        if (accessToken != null) {
            requestSpecification.header("Authorization", accessToken);
        }
        Response response =
                requestSpecification
                        .and()
                        .body(updateUser)
                        .patch(hand_update);
        return response;
    }

    @Step("Изменение данных пользователя без токена")
    public static Response updateUser(UpdateUser updateUser) {
        return updateUser(updateUser, null);

    }
}
