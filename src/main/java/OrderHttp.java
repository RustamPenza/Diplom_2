import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OrderHttp extends BaseHttpClient {

    @Step("Создание заказа")
    public Response createOrder(Map<String, List<String>> ingredients, User user) {
        return doPostRequest(URL.CREATE_ORDER, ingredients, user.getAccessToken());
    }

    @Step("Получение списка заказов пользователя")
    public Response getOrders(User user) {
        return doGetRequest(URL.GET_ORDERS, user.getAccessToken());
    }

    @Step("Получение списка всех доступных ингредиентов")
    public List<Ingredient> getIngredients(User user) {
        Response response = doGetRequest(URL.GET_INGREDIENTS, user.getAccessToken());
        return response.getBody().jsonPath().getList("data", Ingredient.class);
    }


}
