package api;

import api.jsonObjects.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest extends Header {

    CloseableHttpClient client;
    String baseUrl = "http://dev.phonebook-2.telran-edu.de/api";
    String userUrl = "http://dev.phonebook-2.telran-edu.de/api/user";
    String contactUrl = "http://dev.phonebook-2.telran-edu.de/api/contact";
    String email = "test@mail.com";
    String password = "12345678";
    String tempPass = "helloWorld";
    HttpPost postRequest;
    HttpResponse response;
    HttpGet getRequest;
    HttpPut putRequest;
    ObjectMapper objectMapper = new ObjectMapper();

    public UserTest() {
    }

    @Before
    public void init() {
        client = HttpClientBuilder.create().build();
    }

    private void userAuthorization(String token) throws IOException {
        getRequest = new HttpGet(userUrl);
        getRequest.setHeader("access-token", token);

        response = client.execute(getRequest);
    }

    @Test // user login : valid user
    public void test001_loginValidExistingUser() throws IOException {
        postRequest = new HttpPost(userUrl + "/login");
        postRequest.addHeader("Content-Type", "application/json");

        String json = "{\"email\":\"" + email + "\"" + "," + "\"password\":\"" + password + "\"}";

        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        String token = response.getFirstHeader("Access-Token").getValue();

        assertEquals(200, response.getStatusLine().getStatusCode());

        userAuthorization(token);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }


@Test // user login : valid user, email to upper case
    public void test002_loginValidExistingUserUpperCase() throws IOException {
        postRequest = new HttpPost(userUrl + "/login");
        postRequest.addHeader("Content-Type", "application/json");

        String email = "Test@mail.com";

        String json = "{\"email\":\"" + email + "\"" + "," + "\"password\":\"" + password + "\"}";

        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        String token = response.getFirstHeader("Access-Token").getValue();

        assertEquals(200, response.getStatusLine().getStatusCode());

        userAuthorization(token);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test003_changePassAuthUser() throws IOException {
        putRequest = new HttpPut(userUrl + "/password/auth");
        makeHeader(email, password, putRequest);

        String json = "{\"password\":\"" + tempPass + "\"}";

        putRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test // user login : invalid user, password to lower case
    public void test004_loginWrongPassword() throws IOException {

        client = HttpClientBuilder.create().build();

        postRequest = new HttpPost(userUrl + "/login");
        postRequest.addHeader("Content-Type", "application/json");

        String wrongPass = "helloworld";
        String json = "{\"email\":\"" + email + "\"" + "," + "\"password\":\"" + wrongPass + "\"}";

        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test005_changeBackPassAuthUser() throws IOException {
        putRequest = new HttpPut(userUrl + "/password/auth");
        makeHeader(email, tempPass, putRequest);

        String json = "{\"password\":\"" + password + "\"}";

        putRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(putRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test006_addUserProfile() throws IOException {
        putRequest = new HttpPut(contactUrl + "/profile");
        makeHeader(email, password, putRequest);

        UserProfile profile = new UserProfile("Inna", "Drukerman", "Some description");
        String profileJson = objectMapper.writeValueAsString(profile);

        putRequest.setEntity(new StringEntity(profileJson, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test007_getUserProfile() throws IOException {
        getRequest = new HttpGet(contactUrl + "/profile");
        makeHeader(email, password, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String userProfile = EntityUtils.toString(entity, "UTF-8");

        UserProfile profile = new UserProfile(1, "Inna", "Drukerman", "Some description");
        String expectedProfile = objectMapper.writeValueAsString(profile);

        assertEquals(objectMapper.readTree(expectedProfile), objectMapper.readTree(userProfile));
    }
}
