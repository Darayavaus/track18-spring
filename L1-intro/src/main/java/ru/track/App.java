package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

/**
 * TASK:
 * POST request to  https://guarded-mesa-31536.herokuapp.com/track
 * fields: name,github,email
 *
 * lib is included in maven
 * LIB: http://unirest.io/java.html
 *
 *
 */
public class App {

    public static final String URL = "http://guarded-mesa-31536.herokuapp.com/track";
    public static final String FIELD_NAME = "ChernyukDaria";
    public static final String FIELD_GITHUB = "Darayavaus";
    public static final String FIELD_EMAIL = "pulmenti@yandex.ru";

    public static void main(String[] args) throws Exception {
        // 1) Use Unirest.post()
        // 2) Get response .asJson()
        // 3) Get json body and JsonObject
        // 4) Get field "success" from JsonObject

        boolean success = false;
        HttpResponse<JsonNode> jsonResponse = Unirest.post(URL)
                .field("name", FIELD_NAME)
                .field("github", FIELD_GITHUB)
                .field("email", FIELD_EMAIL)
                .asJson();
        JSONObject arrResponse = jsonResponse.getBody().getObject();
        success = arrResponse.getBoolean("success");

        System.out.println(success);

    }

}
