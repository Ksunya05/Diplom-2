package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import params.CreateOrder;

import static io.restassured.RestAssured.given;

public class OrderApi {
    private static final String hand_create_order = "/api/orders";
    private static final String hand_get_orders = "/api/orders";
    private static final String hand_get_ingredients = "/api/ingredients";

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
                        .post(hand_create_order);
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
                        .get(hand_get_ingredients);
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
                        .get(hand_get_orders);
        return response;
    }

    @Step("Получение заказов неавторизованного пользователя")
    public static Response getOrdersWithoutToken() {
        return getOrders(null);
    }

}
