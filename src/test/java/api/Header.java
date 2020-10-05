package api;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class Header {

    CloseableHttpClient client;
    String userUrl = "http://dev.phonebook-2.telran-edu.de/api/user";
    HttpPost post;
    HttpResponse response;

    private String getToken(String email, String password) throws IOException {
        client = HttpClientBuilder.create().build();

        post = new HttpPost(userUrl + "/login");
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
