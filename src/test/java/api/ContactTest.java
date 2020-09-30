package api;

import api.jsonObjects.Address;
import api.jsonObjects.Phone;
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
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContactTest extends Login {

    CloseableHttpClient client;
    String baseUrl = "http://localhost:8080/api";
    String userUrl = "http://localhost:8080/api/user";
    String contactUrl = "http://localhost:8080/api/contact";
    String email = "test@mail.com";
    String password = "12345678";
    HttpPost postRequest;
    HttpResponse response;
    HttpGet getRequest;
    HttpDelete deleteRequest;
    HttpPut putRequest;
    ObjectMapper objectMapper = new ObjectMapper();

    public ContactTest() {
    }

    @Before
    public void init() {
        client = HttpClientBuilder.create().build();
    }

    private int getId(String entity) throws IOException {
        client = HttpClientBuilder.create().build();
        getRequest = new HttpGet(contactUrl + "/" + 1 + "/" + entity);
        makeHeader(email, password, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8").replace("[", "");
        System.out.println(getAll);

        return new JSONObject(getAll).getInt("id");
    }

    @Test
    public void test001_addPhone() throws IOException {
        postRequest = new HttpPost(baseUrl + "/phone");
        makeHeader(email, password, postRequest);

        Phone phone = new Phone(49, 111111, 1);
        String phoneAsString = objectMapper.writeValueAsString(phone);

        postRequest.setEntity(new StringEntity(phoneAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test002_getExistingPhone() throws IOException {
        int phoneId = getId("phones");

        getRequest = new HttpGet(baseUrl + "/phone/" + phoneId);
        makeHeader(email, password, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String phone = EntityUtils.toString(entity, "UTF-8");

        Phone phoneJson = new Phone(49, 111111, phoneId, 1);
        String expectedPhone = objectMapper.writeValueAsString(phoneJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedPhone), objectMapper.readTree(phone));
    }

    @Test
    public void test003_updatePhone() throws IOException {

        int phoneId = getId("phones");

        putRequest = new HttpPut(baseUrl + "/phone");
        makeHeader(email, password, putRequest);

        Phone phoneJson = new Phone(49, 2222222, phoneId, 1);
        String phoneAsString = objectMapper.writeValueAsString(phoneJson);
        putRequest.setEntity(new StringEntity(phoneAsString, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test004_getUpdatedPhone() throws IOException {
        int phoneId = getId("phones");

        getRequest = new HttpGet(baseUrl + "/phone/" + phoneId);
        makeHeader(email, password, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String phone = EntityUtils.toString(entity, "UTF-8");

        Phone phoneJson = new Phone(49, 2222222, phoneId, 1);
        String expectedPhone = objectMapper.writeValueAsString(phoneJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedPhone), objectMapper.readTree(phone));
    }

    @Test
    public void test005_deletePhone() throws IOException {
        int phoneId = getId("phones");

        deleteRequest = new HttpDelete(baseUrl + "/phone/" + phoneId);
        makeHeader(email, password, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test006_getNotExistingPhone() throws IOException {
        getRequest = new HttpGet(baseUrl + "/phone/1111");
        makeHeader(email, password, getRequest);

        response = client.execute(getRequest);
        assertEquals(500, response.getStatusLine().getStatusCode());
    }
}
