package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import params.CreateOrder;
import params.CreateUser;
import params.DeleteUser;
import steps.BasePage;
import steps.OrderApi;
import steps.UserApi;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class GetOrderTest extends BasePage {
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
        CreateOrder createOrder = new CreateOrder(new String[]{ingredientOne, ingredientTwo});
        OrderApi.createOrder(createOrder, accessToken);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void testGetOrders() {
        Response response = OrderApi.getOrders(accessToken);
        response
                .then().assertThat().statusCode(SC_OK).body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void testGetOrdersWithoutToken() {
        Response response = OrderApi.getOrdersWithoutToken();
        response
                .then().assertThat().statusCode(SC_UNAUTHORIZED).body("success", equalTo(false));
    }

    @After
    public void deleteUser() {
        UserApi.deleteUser(new DeleteUser(email, name), accessToken).then().statusCode(SC_ACCEPTED);
    }
}
