import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class OrderHttpTest {

    private final UserHttp userHttp = new UserHttp();
    private User user;

    private final OrderHttp orderHttp = new OrderHttp();
    private final List<String> ingredientIds = new ArrayList<>();

    private final Map<String, List<String>> ingredients = new HashMap<>();


    @Before
    public void setUp() {
        user = new User("fake5446454@mail.ru", "123456", "Alesha");
        userHttp.createUser(user);
        ingredientIds.add("61c0c5a71d1f82001bdaaa72");
        ingredientIds.add("61c0c5a71d1f82001bdaaa6f");
        ingredientIds.add("61c0c5a71d1f82001bdaaa70");
        ingredients.put("ingredients", ingredientIds);
    }

    @After
    public void tearDown() {
        if (user.getAccessToken() != null) {
            userHttp.deleteUser(user);
        }
    }

    @Test
    @DisplayName("Создаем заказ с авторизацией и ингредиентами")
    @Description("Проверяем, что код ответа 200 и сверяем список ингредиентов")
    public void createOrderAuthorizedUser() {
        Response response = orderHttp.createOrder(ingredients, user);
        assertEquals(200, response.getStatusCode());

        JsonPath jsonPath = response.jsonPath(); // из ответа сделали джейсонину, что бы по ней удобнее было перемещаться и брать нужные значения
        List<String> _id = jsonPath.get("order.ingredients._id"); // взяли из ответа все айдишники ингредиентов

        //Проверяем, что в ответе приходят теже айдишники ингредиентов, которые мы передали в запросе
        for (int i = 0; i < _id.size(); i++) {
            assertEquals(ingredientIds.get(i), _id.get(i));
        }
    }

    @Test
    @DisplayName("Создаем заказ без авторизации")
    @Description("Проверяем, что код ответа 200")
    //в документации не указано подведение для данного случая, поэтому сделал по факту
    public void createOrderNotAuthorizedUser() {
        String token = user.getAccessToken();
        user.setAccessToken("");
        Response response = orderHttp.createOrder(ingredients, user);
        assertEquals(200, response.statusCode());
        user.setAccessToken(token); //необходимо восстановить токен юзера, что бы сработало его удаление после теста
    }

    @Test
    @DisplayName("Создаем заказ с авторизацией, но без ингредиентов")
    @Description("Проверяем, что код ответа 400 и сообщение ошибки")
    public void createOrderWithoutIngredients() {
        ingredients.clear();
        Response response = orderHttp.createOrder(ingredients, user);
        String expectedMessage = "Ingredient ids must be provided";
        String actualMessage = response.getBody().jsonPath().get("message");
        assertEquals(400, response.statusCode());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Создаем заказ с неверным хешем ингредиента")
    @Description("Проверяем, что код ответа 500")
    public void createOrderWithoutWrongHashIngredients() {
        ingredientIds.add("123456789");
        ingredients.put("ingredients", ingredientIds);
        Response response = orderHttp.createOrder(ingredients, user);
        assertEquals(500, response.statusCode());

    }

    @Test
    @DisplayName("Получение списка заказов конкретного авторизованного пользователя")
    @Description("Проверяем, что код ответа 200 и проверяем список ингредиентов в заказе")
    public void getOrdersAuthorizedUser() {
        orderHttp.createOrder(ingredients, user);
        Response response = orderHttp.getOrders(user);
        assertEquals(200, response.getStatusCode());

        JsonPath jsonPath = response.jsonPath();
        List<String> actualId = jsonPath.get("orders.ingredients"); // взяли из ответа все айдишники ингредиентов

        /*
        ниже выполняется проверка, что ингредиенты в запросе и в ответе совпадают, сделано на мой взгляд костыльно,
        но я не смог пройтись по массиву, который хранится в actualId.get(0)
        */
        List<List<String>> expectedId = new ArrayList<>();
        expectedId.add(ingredientIds);
        assertEquals(expectedId, actualId);
    }

    @Test
    @DisplayName("Получение списка заказов конкретного Не авторизованного пользователя")
    @Description("Проверяем, что код ответа 401 и проверяем текст ошибки")
    public void getOrdersUnAuthorizedUser() {
        orderHttp.createOrder(ingredients, user);
        String token = user.getAccessToken();
        user.setAccessToken("");
        Response response = orderHttp.getOrders(user);
        assertEquals(401, response.getStatusCode());
        String expectedMessage = "You should be authorised";
        String actualMessage = response.getBody().jsonPath().get("message");
        assertEquals(expectedMessage, actualMessage);
        user.setAccessToken(token);
    }
}