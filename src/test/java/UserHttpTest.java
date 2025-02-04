import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserHttpTest {
    private final UserHttp userHttp = new UserHttp();
    private User user;

    @Before
    public void setUp() {
        user = new User("fake13@mail.ru", "123456", "Alesha");
    }

    @After
    public void tearDown() {
        if (user.getAccessToken() != null) {
            userHttp.loginUser(user);
            userHttp.deleteUser(user);
        }
    }

    @Test
    @DisplayName("Создание курьера со всеми обязательными полями")
    @Description("Проверяем, что код ответа 201 и токен не null")
    public void createUser() {
        Response response = userHttp.createUser(user);
        assertEquals(200, response.statusCode());
        assertNotNull(response.getBody().jsonPath().get("accessToken"));
    }

    @Test
    @DisplayName("Создание курьера, который уже существует")
    @Description("Проверяем, что код ответа 403 и сообщение ошибки")
    public void createUserDuplicateError403() {
        Response response = userHttp.createUser(user);
        assertEquals(200, response.statusCode());
        Response response2 = userHttp.createUser(user);
        String expectedMessage = "User already exists";
        String actualMessage = response2.jsonPath().get("message");
        assertEquals(403, response2.statusCode());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Создание курьера без поля email")
    @Description("Проверяем, что код ответа 403 и сообщение ошибки")
    public void createUserWithoutEmailError403() {
        user.setEmail("");
        Response response = userHttp.createUser(user);
        String expectedMessage = "Email, password and name are required fields";
        String actualMessage = response.jsonPath().get("message");
        assertEquals(403, response.statusCode());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Создание курьера без поля password")
    @Description("Проверяем, что код ответа 403 и сообщение ошибки")
    public void createUserWithoutPasswordError403() {
        user.setPassword("");
        Response response = userHttp.createUser(user);
        String expectedMessage = "Email, password and name are required fields";
        String actualMessage = response.jsonPath().get("message");
        assertEquals(403, response.statusCode());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Создание курьера без поля name")
    @Description("Проверяем, что код ответа 403 и сообщение ошибки")
    public void createUserWithoutNameError403() {
        user.setName("");
        Response response = userHttp.createUser(user);
        String expectedMessage = "Email, password and name are required fields";
        String actualMessage = response.jsonPath().get("message");
        assertEquals(403, response.statusCode());
        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    @DisplayName("Авторизация под существующим пользователем")
    @Description("Проверяем, что ответ возвращается с кодом 200")
    public void loginRealUserOk200() {
        userHttp.createUser(user);
        Response response = userHttp.loginUser(user);
        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Авторизация под не существующим пользователем")
    @Description("Проверяем, что код ответа 401 и сообщение ошибки")
    public void loginUnrealUserError401() {
        Response response = userHttp.loginUser(user);
        String expectedMessage = "email or password are incorrect";
        String actualMessage = response.getBody().jsonPath().get("message");
        assertEquals(401, response.statusCode());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Измененние данных авторизованным пользователем")
    @Description("Проверяем, что код ответа 200 и данные изменились")
    public void changeAuthUser() {
        userHttp.createUser(user);
        userHttp.loginUser(user);
        user.setEmail("changed1@mail.ru");
        user.setName("new Name");
        Response response = userHttp.changeUser(user);
        assertEquals(200, response.statusCode());
        assertEquals(user.getEmail(), response.getBody().jsonPath().get("user.email"));
        assertEquals(user.getName(), response.getBody().jsonPath().get("user.name"));
    }

    @Test
    @DisplayName("Измененние данных не авторизованным пользователем")
    @Description("Проверяем, что код ответа 401 и сообщение ошибки")
    public void changeNoAuthUserError401() {
        userHttp.createUser(user);
        String token = user.getAccessToken();
        user.setEmail("changed@mail.ru");
        user.setName("new Name");
        user.setAccessToken("");
        Response response = userHttp.changeUser(user);
        String expectedMessage = "You should be authorised";
        String actualMessage = response.getBody().jsonPath().get("message");
        assertEquals(401, response.statusCode());
        assertEquals(expectedMessage, actualMessage);
        user.setAccessToken(token);

    }

}