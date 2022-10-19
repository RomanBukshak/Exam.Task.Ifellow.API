package StepDefinition;

import io.cucumber.java.ru.Затем;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static utils.Configuration.getConfigurationValue;


public class TestApiReqres {
    @Затем("Отправить запрос на reqres и сравнить результат")
    public static void sendBody() throws IOException {
        JSONObject body = new JSONObject(new String(Files.readAllBytes(Paths.get("src/test/java/Json/reqres.json"))));
        Response createUser = given()
                .contentType(ContentType.JSON)
                .body(body.put("name", "Tomato"))
                .body(body.put("job", "Eat maket"))
                .body(body.toString())
                .baseUri(getConfigurationValue("baseUriReqres"))
                .when()
                .post("users")
                .then()
                .statusCode(201)
                .log().all()
                .extract().response();
        JSONObject createNewUser = new JSONObject(createUser.getBody().asString());
        Assertions.assertEquals(createNewUser.getString("name"), body.getString("name"), "Имена не совпадают!");
        Assertions.assertEquals(createNewUser.getString("job"), body.getString("job"), "Работы не совпадают!");
        Assertions.assertNotNull(createNewUser.getString("id"),"Id пустое!");
        Assertions.assertNotNull(createNewUser.getString("createdAt"),"Дата создания пустая!");
    }

}
