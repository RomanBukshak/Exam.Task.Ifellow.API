package StepDefinition;

import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Затем;
import io.cucumber.java.ru.И;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;
import static utils.Configuration.getConfigurationValue;

public class TestApiStepsRickAndMorty {
    public static String mortySmithLocation;
    public static int mortySmithLastEpisode;
    public static String mortySmithSpecies;
    public static int lastEpisodeID;
    public static String lastEpisodeName;
    public static int lastCharacterInLastEpisodeID;
    public static String lastCharacterInLastEpisodeName;
    public static String lastCharacterInLastEpisodeSpecies;
    public static String lastCharacterInLastEpisodeLocation;

    @Дано("Получить информацию о Морти Смит")
    public static void getInfoAboutMortySmith(int id) {
        Response findInfoAboutMortySmith = given()
                .baseUri(getConfigurationValue("baseUriRickAndMorty"))
                .contentType(ContentType.JSON)
                .when().get("character/" + id)
                .then()
                .assertThat()
                .statusCode(200)
                .log().all()
                .extract().response();
        JSONObject mortySmith = new JSONObject(findInfoAboutMortySmith.getBody().asString());
        mortySmithLocation = mortySmith.getJSONObject("location").get("name").toString();
        mortySmithSpecies = mortySmith.get("species").toString();
        int mortySmithNumberLastEpisode = mortySmith.getJSONArray("episode").length()-1;
        mortySmithLastEpisode = Integer.parseInt(mortySmith.getJSONArray("episode").get(mortySmithNumberLastEpisode).toString().replaceAll("[^0-9]",""));
    }

    @Затем("Найти последний эпизод")
    public static void findLastEpisode() {
        Response findLastEpisode = given()
                .baseUri(getConfigurationValue("baseUriRickAndMorty"))
                .contentType(ContentType.JSON)
                .when().get("episode")
                .then()
                .assertThat()
                .statusCode(200)
                .log().all()
                .extract().response();
        lastEpisodeID = (int) new JSONObject(findLastEpisode.getBody().asString()).getJSONObject("info").get("count");
    }

    @Затем("Найти последнего персонажа последнего эпизода")
    public static void findLastCharacterIDInLastEpisode() {
        Response findLastCharacterIDInLastEpisode = given()
                .baseUri(getConfigurationValue("baseUriRickAndMorty"))
                .contentType(ContentType.JSON)
                .when().get("episode/" + lastEpisodeID)
                .then()
                .assertThat()
                .statusCode(200)
                .log().all()
                .extract().response();
        lastEpisodeName = new JSONObject(findLastCharacterIDInLastEpisode.getBody().asString()).get("name").toString();
        JSONArray characterInLastEpisode = new JSONObject(findLastCharacterIDInLastEpisode.getBody().asString()).getJSONArray("characters");
        int lastCharacterInLastEpisodeNumber = characterInLastEpisode.length()-1;
        lastCharacterInLastEpisodeID = Integer.parseInt(characterInLastEpisode.get(lastCharacterInLastEpisodeNumber).toString().replaceAll("[^0-9]",""));
    }
    @Затем("Получить информацию о последнем персонаже")
    public static void infoLastCharacterInLastEpisode() {
        Response infoLastCharacterInLastEpisode = given()
                .baseUri(getConfigurationValue("baseUriRickAndMorty"))
                .contentType(ContentType.JSON)
                .when().get("character/" + lastCharacterInLastEpisodeID)
                .then()
                .assertThat()
                .statusCode(200)
                .log().all()
                .extract().response();
        lastCharacterInLastEpisodeName = new JSONObject(infoLastCharacterInLastEpisode.getBody().asString()).get("name").toString();
        lastCharacterInLastEpisodeSpecies = new JSONObject(infoLastCharacterInLastEpisode.getBody().asString()).get("species").toString();
        lastCharacterInLastEpisodeLocation =  new JSONObject(infoLastCharacterInLastEpisode.getBody().asString()).getJSONObject("location").get("name").toString();
    }

    @И("Проверить совпадения локации")
    public static void assertSpecies() {
        Assertions.assertEquals(mortySmithSpecies, lastCharacterInLastEpisodeSpecies, "Персонажи разной расы!");
    }

    @И("Проверить совпадения расы")
    public static void assertRace() {
        Assertions.assertEquals(mortySmithLocation, lastCharacterInLastEpisodeLocation, "Персонажи находятся в разных местах!");
    }

}
