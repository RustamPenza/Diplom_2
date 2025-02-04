import io.qameta.allure.Step;
import io.restassured.response.Response;

public class OrderHttp extends BaseHttpClient {

    @Step("Создание заказа")
    public Response createOrderLoginUser(Order order, User user) {
        Response response = doPostRequest(URL.CREATE_ORDER, order, user.getAccessToken());
        order.setIngredients(response.getBody().jsonPath().get("ingredients"));
        return response;
    }


}
