import io.qameta.allure.Step;
import io.restassured.response.Response;

public class UserHttp extends BaseHttpClient {

    @Step("Создание пользователя")
    public Response createUser(User user) {
        Response response = doPostRequest(URL.CREATE_USER, user);
        if(user.getAccessToken() == null) {
            user.setAccessToken(response.getBody().jsonPath().get("accessToken"));
        }
        return response;
    }

    @Step("Авторизация пользователя")
    public Response loginUser(User user) {
        return doPostRequest(URL.LOGIN_USER, user);
    }

    @Step("Изменение пользователя")
    public Response changeUser(User user) {
        return doPatchRequest(URL.CHANGE_USER, user, user.getAccessToken());
    }

    @Step("Удаление пользователя")
    public Response deleteUser (User user) {
        return doDeleteRequest(URL.DELETE_USER, user.getAccessToken());
    }
}
