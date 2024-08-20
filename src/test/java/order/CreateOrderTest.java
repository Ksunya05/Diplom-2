package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import params.CreateOrder;
import params.CreateUser;
import params.DeleteUser;
import base.BaseTest;
import steps.OrderApi;
import steps.UserApi;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CreateOrderTest extends BaseTest {
    private String email;
    private String name;
    private String accessToken;
    private String ingredientOne;
    private String ingredientTwo;
    private CreateUser user;

    @Before
    public void setUp() {
        baseURL();
        user = CreateUser.makeRandomUser();
        Response response = UserApi.createUser(user);
        email = user.getEmail();
        name = user.getName();
        accessToken = response.jsonPath().getString("accessToken");
        response = OrderApi.getIngredients();
        ingredientOne = response.jsonPath().getString("data[0]._id");
        ingredientTwo = response.jsonPath().getString("data[1]._id");
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void testCreateOrder() {
        CreateOrder createOrder = new CreateOrder(new String[]{ingredientOne, ingredientTwo});
        Response response = OrderApi.createOrder(createOrder, accessToken);
        response
                .then().assertThat().statusCode(SC_OK).body("order.ingredients[0]._id", is(ingredientOne))
                .body("order.ingredients[1]._id", is(ingredientTwo)).body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutToken() {
        CreateOrder createOrder = new CreateOrder(new String[]{ingredientOne, ingredientTwo});
        Response response = OrderApi.createOrder(createOrder);
        response
                .then().assertThat().statusCode(SC_UNAUTHORIZED).body("success", equalTo(false));

    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        CreateOrder createOrder = new CreateOrder(new String[]{});
        Response response = OrderApi.createOrder(createOrder, accessToken);
        response
                .then().assertThat().statusCode(SC_BAD_REQUEST).body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа с неверным хэшем ингредиентов")
    public void testCreateOrderWithWrongIngredients() {
        String wrongIngredient = RandomStringUtils.randomAlphabetic(24);
        CreateOrder createOrder = new CreateOrder(new String[]{wrongIngredient});
        Response response = OrderApi.createOrder(createOrder, accessToken);
        response
                .then().assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void deleteUser() {

        UserApi.deleteUser(new DeleteUser(email, name), accessToken).then().statusCode(SC_ACCEPTED);
    }
}


