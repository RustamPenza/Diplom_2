import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseHttpClient {

    private final RequestSpecification baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.BASE_URL)
            .addHeader("Content-type", "application/json")
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .setRelaxedHTTPSValidation()
            .build();

        protected Response doGetRequest(String path, String token) {
        return given()
                .spec(baseRequestSpec)
                .auth().oauth2(token)
                .get(path)
                .thenReturn();
    }

    protected Response doPostRequest(String path, Object body) {
        return given()
                .spec(baseRequestSpec)
                .body(body)
                .post(path)
                .thenReturn();
    }

    protected Response doPostRequest(String path, Object body, String token) {
        return given()
                .spec(baseRequestSpec)
                .auth().oauth2(token)
                .body(body)
                .post(path)
                .thenReturn();
    }

    protected Response doPatchRequest(String path, Object body, String token) {
        return given()
                .spec(baseRequestSpec)
                .header("Authorization", token)
                .body(body)
                .patch(path)
                .thenReturn();
    }

    protected Response doDeleteRequest(String path, String token) {
        return given()
                .spec(baseRequestSpec)
                .header("Authorization", token)
                .delete(path)
                .thenReturn();
    }
}
