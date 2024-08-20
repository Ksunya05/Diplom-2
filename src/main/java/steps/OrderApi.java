package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import params.CreateOrder;

import static io.restassured.RestAssured.given;

public class OrderApi {
    private static final String CREATE_ORDER = "/api/orders";
    private static final String GET_ORDERS = "/api/orders";
    private static final String GET_INGREDIENTS = "/api/ingredients";

    @Step("Создание заказа")
    public static Response createOrder(CreateOrder createOrder, String accessToken) {
        RequestSpecification requestSpecification =
                given().log().all()
                        .header("Content-type", "application/json");
        if (accessToken != null) {
            requestSpecification.header("Authorization", accessToken);
        }
        Response response =
                requestSpecification
                        .and()
                        .body(createOrder)
                        .post(CREATE_ORDER);
        return response;

    }

    @Step("Создание заказа без авторизации")
    public static Response createOrder(CreateOrder createOrder) {
        return createOrder(createOrder, null);
    }

    @Step("Получение ингредиентов")
    public static Response getIngredients() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get(GET_INGREDIENTS);
        return response;
    }

    @Step("Получение заказов")
    public static Response getOrders(String accessToken) {
        RequestSpecification requestSpecification =
                given()
                        .header("Content-type", "application/json");
        if (accessToken != null) {
            requestSpecification.header("Authorization", accessToken);
        }
        Response response =
                requestSpecification
                        .and()
                        .when()
                        .get(GET_ORDERS);
        return response;
    }

    @Step("Получение заказов неавторизованного пользователя")
    public static Response getOrdersWithoutToken() {
        return getOrders(null);
    }

}
