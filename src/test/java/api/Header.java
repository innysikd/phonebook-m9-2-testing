package api;

import com.jayway.restassured.response.Response;
import static com.jayway.restassured.RestAssured.given;
//import com.jayway.restassured.http.ContentType;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

import static api.Constants.USER_URL;

public class Header {

    CloseableHttpClient client;
    HttpPost post;
    HttpResponse response;

//    public static String retrieveToken (String email, String password){
//        String json = "{\"email\":\"" + email + "\"" + "," + "\"password\":\"" + password + "\"}";
//
//        String response =
//                given()
//                .contentType(com.jayway.restassured.http.ContentType.JSON)
//                .header("Content-Type", "application/json")
//                .body(json)
//                .when()
//                .post(USER_URL + "/login")
//                .then()
//                .statusCode(200)
//                .extract()
//                .response()
//                .getHeaders()
//                .getValue("Access-Token");
//
//        return response;
//    }

    private String getToken(String email, String password) throws IOException {
        client = HttpClientBuilder.create().build();

        post = new HttpPost(USER_URL + "/login");
        post.addHeader("Content-Type", "application/json");

        String json = "{\"email\":\"" + email + "\"" + "," + "\"password\":\"" + password + "\"}";

        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(post);

        return response.getFirstHeader("Access-Token").getValue();
    }

    public void makeHeader (String email, String password, HttpRequest request) throws IOException {
        request.addHeader("Content-Type", "application/json");
        request.setHeader("access-token", getToken(email, password));
    }
}
