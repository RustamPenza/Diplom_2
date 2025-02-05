import io.qameta.allure.Step;
import io.restassured.response.Response;

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


}
