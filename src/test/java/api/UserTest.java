package api;

import api.jsonObjects.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;
import static com.jayway.restassured.RestAssured.given;
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
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static api.Constants.*;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {

    CloseableHttpClient client;
    String tempPass = "helloWorld";
    HttpPost postRequest;
    HttpResponse response;
    HttpGet getRequest;
    HttpPut putRequest;
    ObjectMapper objectMapper = new ObjectMapper();
    Header header = new Header();
    private static String token;

    public UserTest() {
    }

    @Before
    public void init() {
        client = HttpClientBuilder.create().build();
    }

//    @BeforeClass
//    public static void init() {
//        token = Header.retrieveToken(EMAIL_USER1, PASS_USER1);
//    }

    private void userAuthorization(String token) throws IOException {
        getRequest = new HttpGet(USER_URL);
        getRequest.setHeader("access-token", token);

        response = client.execute(getRequest);
    }

    @Test // user login : valid user
    public void test001_loginValidExistingUser() throws IOException {
        postRequest = new HttpPost(USER_URL + "/login");
        postRequest.addHeader("Content-Type", "application/json");

        String json = "{\"email\":\"" + EMAIL_USER1 + "\"" + "," + "\"password\":\"" + PASS_USER1 + "\"}";

        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        String token = response.getFirstHeader("Access-Token").getValue();

        assertEquals(200, response.getStatusLine().getStatusCode());

        userAuthorization(token);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }


@Test // user login : valid user, email to upper case
    public void test002_loginValidExistingUserUpperCase() throws IOException {
        postRequest = new HttpPost(USER_URL + "/login");
        postRequest.addHeader("Content-Type", "application/json");

        String email = "Test@mail.com";

        String json = "{\"email\":\"" + email + "\"" + "," + "\"password\":\"" + PASS_USER1 + "\"}";

        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        String token = response.getFirstHeader("Access-Token").getValue();

        assertEquals(200, response.getStatusLine().getStatusCode());

        userAuthorization(token);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test003_changePassAuthUser() throws IOException {
        putRequest = new HttpPut(USER_URL + "/password/auth");
        header.makeHeader(EMAIL_USER1, PASS_USER1, putRequest);

        String json = "{\"password\":\"" + tempPass + "\"}";

        putRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test // user login : invalid user, password to lower case
    public void test004_loginWrongPassword() throws IOException {

        client = HttpClientBuilder.create().build();

        postRequest = new HttpPost(USER_URL + "/login");
        postRequest.addHeader("Content-Type", "application/json");

        String wrongPass = "helloworld";
        String json = "{\"email\":\"" + EMAIL_USER1 + "\"" + "," + "\"password\":\"" + wrongPass + "\"}";

        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test005_changeBackPassAuthUser() throws IOException {
        putRequest = new HttpPut(USER_URL + "/password/auth");
        header.makeHeader(EMAIL_USER1, tempPass, putRequest);

        String json = "{\"password\":\"" + PASS_USER1 + "\"}";

        putRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        response = client.execute(putRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test006_addUserProfile() throws IOException {
        putRequest = new HttpPut(CONTACT_URL + "/profile");
        header.makeHeader(EMAIL_USER1, PASS_USER1, putRequest);

        UserProfile profile = new UserProfile("Inna", "Drukerman", "Some description");
        String profileJson = objectMapper.writeValueAsString(profile);

        putRequest.setEntity(new StringEntity(profileJson, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test007_getUserProfile() throws IOException {
        getRequest = new HttpGet(CONTACT_URL + "/profile");
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String userProfile = EntityUtils.toString(entity, "UTF-8");

        UserProfile profile = new UserProfile(1, "Inna", "Drukerman", "Some description");
        String expectedProfile = objectMapper.writeValueAsString(profile);

        assertEquals(objectMapper.readTree(expectedProfile), objectMapper.readTree(userProfile));
    }
}
