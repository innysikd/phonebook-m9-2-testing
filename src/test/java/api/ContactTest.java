package api;

import api.jsonObjects.Address;
import api.jsonObjects.Contact;
import api.jsonObjects.Email;
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
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static api.Constants.*;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContactTest {

    CloseableHttpClient client;
    private HttpPost postRequest;
    private HttpResponse response;
    private HttpGet getRequest;
    private HttpDelete deleteRequest;
    private HttpPut putRequest;
    private ObjectMapper objectMapper = new ObjectMapper();
    Header header = new Header();
    
    public ContactTest() {
    }

    @Before
    public void init() {
        client = HttpClientBuilder.create().build();
    }

    @After
    public void close() throws IOException {
        client.close();
    }

    private int getId(String endPoint) throws IOException {
        client = HttpClientBuilder.create().build();
        getRequest = new HttpGet(CONTACT_URL + endPoint);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8").replace("[", "");

        return new JSONObject(getAll).getInt("id");
    }

    //adding and editing contact
    @Test
    public void test001_addContact() throws IOException {
        postRequest = new HttpPost(CONTACT_URL);
        header.makeHeader(EMAIL_USER1, PASS_USER1, postRequest);

        Contact contact = new Contact("Anna", "Yurchenko", "Tel-Ran");
        String contactAsString = objectMapper.writeValueAsString(contact);

        postRequest.setEntity(new StringEntity(contactAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test002_getExistingContact() throws IOException {
        int contactId = getId("");

        getRequest = new HttpGet(CONTACT_URL + "/" + contactId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);

        HttpEntity entity = response.getEntity();
        String contact = EntityUtils.toString(entity, "UTF-8");

        Contact contactJson = new Contact(contactId, "Anna", "Yurchenko", "Tel-Ran");
        String expectedContact = objectMapper.writeValueAsString(contactJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedContact), objectMapper.readTree(contact));
    }

    @Test
    public void test003_changeContact() throws IOException {
        int contactId = getId("");

        putRequest = new HttpPut(CONTACT_URL);
        header.makeHeader(EMAIL_USER1, PASS_USER1, putRequest);

        Contact contact = new Contact(contactId, "Anna", "Yurchenko", "Friend");
        String contactAsString = objectMapper.writeValueAsString(contact);

        putRequest.setEntity(new StringEntity(contactAsString));
        response = client.execute(putRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test004_getChangedContact() throws IOException {
        int contactId = getId("");

        getRequest = new HttpGet(CONTACT_URL + "/" + contactId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);

        HttpEntity entity = response.getEntity();
        String contact = EntityUtils.toString(entity, "UTF-8");

        Contact contactJson = new Contact(contactId, "Anna", "Yurchenko", "Friend");
        String expectedContact = objectMapper.writeValueAsString(contactJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedContact), objectMapper.readTree(contact));
    }

    //adding and editing phones
    @Test
    public void test005_addPhone() throws IOException {
        int contactId = getId("");
        postRequest = new HttpPost(PHONE_URL);
        header.makeHeader(EMAIL_USER1, PASS_USER1, postRequest);

        Phone phone = new Phone(49, 5768696, contactId);
        String phoneAsString = objectMapper.writeValueAsString(phone);

        postRequest.setEntity(new StringEntity(phoneAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test006_getExistingPhone() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        getRequest = new HttpGet(PHONE_URL + "/" + phoneId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String phone = EntityUtils.toString(entity, "UTF-8");

        Phone phoneJson = new Phone(49, 5768696, phoneId, contactId);
        String expectedPhone = objectMapper.writeValueAsString(phoneJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedPhone), objectMapper.readTree(phone));
    }

    @Test
    public void test007_changePhone() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        putRequest = new HttpPut(PHONE_URL);
        header.makeHeader(EMAIL_USER1, PASS_USER1, putRequest);

        Phone phoneJson = new Phone(49, 2222222, phoneId, contactId);
        String phoneAsString = objectMapper.writeValueAsString(phoneJson);
        putRequest.setEntity(new StringEntity(phoneAsString, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test008_getChangedPhone() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        getRequest = new HttpGet(PHONE_URL + "/" + phoneId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String phone = EntityUtils.toString(entity, "UTF-8");

        Phone phoneJson = new Phone(49, 2222222, phoneId, contactId);
        String expectedPhone = objectMapper.writeValueAsString(phoneJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedPhone), objectMapper.readTree(phone));
    }

    @Test
    public void test009_getContactWithPhone() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        getRequest = new HttpGet(CONTACT_URL + "/" + contactId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);

        HttpEntity entity = response.getEntity();
        String contact = EntityUtils.toString(entity, "UTF-8");

        List<Phone> phoneList = new ArrayList<>();
        phoneList.add(new Phone(49, 2222222, phoneId, contactId));

        Contact contactJson = new Contact(contactId, "Anna", "Yurchenko", "Friend", phoneList);
        String expectedContact = objectMapper.writeValueAsString(contactJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedContact), objectMapper.readTree(contact));
    }

    //adding and editing emails
    @Test
    public void test014_addEmail() throws IOException {
        int contactId = getId("");
        postRequest = new HttpPost(EMAIL_URL);
        header.makeHeader(EMAIL_USER1, PASS_USER1, postRequest);

        Email email = new Email("test@email.com", contactId);
        String emailAsString = objectMapper.writeValueAsString(email);

        postRequest.setEntity(new StringEntity(emailAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test015_getExistingEmail() throws IOException {
        int contactId = getId("");
        int emailId = getId("/" + contactId + "/emails");

        getRequest = new HttpGet(EMAIL_URL + "/" + emailId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String email = EntityUtils.toString(entity, "UTF-8");

        Email emailJson = new Email("test@email.com", emailId, contactId);
        String expectedEmail = objectMapper.writeValueAsString(emailJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedEmail), objectMapper.readTree(email));
    }

    @Test
    public void test016_changeEmail() throws IOException {
        int contactId = getId("");
        int emailId = getId("/" + contactId + "/emails");

        putRequest = new HttpPut(EMAIL_URL);
        header.makeHeader(EMAIL_USER1, PASS_USER1, putRequest);

        Email emailJson = new Email("test123@email.de", emailId, contactId);
        String emailAsString = objectMapper.writeValueAsString(emailJson);
        putRequest.setEntity(new StringEntity(emailAsString, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test017_getChangedEmail() throws IOException {
        int contactId = getId("");
        int emailId = getId("/" + contactId + "/emails");

        getRequest = new HttpGet(EMAIL_URL + "/" + emailId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String email = EntityUtils.toString(entity, "UTF-8");

        Email emailJson = new Email("test123@email.de", emailId, contactId);
        String expectedEmail = objectMapper.writeValueAsString(emailJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedEmail), objectMapper.readTree(email));
    }

    @Test
    public void test018_getContactWithPhoneAndEmail() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");
        int emailId = getId("/" + contactId + "/emails");

        getRequest = new HttpGet(CONTACT_URL + "/" + contactId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);

        HttpEntity entity = response.getEntity();
        String contact = EntityUtils.toString(entity, "UTF-8");

        List<Phone> phoneList = new ArrayList<>();
        phoneList.add(new Phone(49, 2222222, phoneId, contactId));

        List<Email> emailList = new ArrayList<>();
        emailList.add(new Email("test123@email.de", emailId, contactId));

        Contact contactJson = new Contact(contactId, "Anna", "Yurchenko", "Friend", phoneList, emailList);
        String expectedContact = objectMapper.writeValueAsString(contactJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedContact), objectMapper.readTree(contact));
    }

    //adding and editing addresses
    @Test
    public void test023_addAddress() throws IOException {
        int contactId = getId("");
        postRequest = new HttpPost(ADDRESS_URL);
        header.makeHeader(EMAIL_USER1, PASS_USER1, postRequest);

        Address address = new Address("Street", "zip", "City", "Country", contactId);
        String addressAsString = objectMapper.writeValueAsString(address);

        postRequest.setEntity(new StringEntity(addressAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test024_getExistingAddress() throws IOException {
        int contactId = getId("");
        int addressId = getId("/" + contactId + "/addresses");

        getRequest = new HttpGet(ADDRESS_URL + "/" + addressId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String address = EntityUtils.toString(entity, "UTF-8");

        Address addressJson = new Address("Street", "zip", "City", "Country", addressId, contactId);
        String expectedAddress = objectMapper.writeValueAsString(addressJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedAddress), objectMapper.readTree(address));
    }

    @Test
    public void test025_changeAddress() throws IOException {
        int contactId = getId("");
        int addressId = getId("/" + contactId + "/addresses");

        putRequest = new HttpPut(ADDRESS_URL);
        header.makeHeader(EMAIL_USER1, PASS_USER1, putRequest);

        Address addressJson = new Address("Street", "zip", "City", "Some other country", addressId, contactId);
        String addressAsString = objectMapper.writeValueAsString(addressJson);
        putRequest.setEntity(new StringEntity(addressAsString, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test026_getChangedAddress() throws IOException {
        int contactId = getId("");
        int addressId = getId("/" + contactId + "/addresses");

        getRequest = new HttpGet(ADDRESS_URL + "/" + addressId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String address = EntityUtils.toString(entity, "UTF-8");

        Address addressJson = new Address("Street", "zip", "City", "Some other country", addressId, contactId);
        String expectedAddress = objectMapper.writeValueAsString(addressJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedAddress), objectMapper.readTree(address));
    }

    @Test
    public void test027_getContactWithPhoneEmailAddress() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");
        int emailId = getId("/" + contactId + "/emails");
        int addressId = getId("/" + contactId + "/addresses");

        getRequest = new HttpGet(CONTACT_URL + "/" + contactId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);

        HttpEntity entity = response.getEntity();
        String contact = EntityUtils.toString(entity, "UTF-8");

        List<Phone> phoneList = new ArrayList<>();
        phoneList.add(new Phone(49, 2222222, phoneId, contactId));

        List<Email> emailList = new ArrayList<>();
        emailList.add(new Email("test123@email.de", emailId, contactId));

        List<Address> addressList = new ArrayList<>();
        addressList.add(new Address("Street", "zip", "City", "Some other country", addressId, contactId));

        Contact contactJson = new Contact(contactId, "Anna", "Yurchenko", "Friend", phoneList, emailList, addressList);
        String expectedContact = objectMapper.writeValueAsString(contactJson);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(objectMapper.readTree(expectedContact), objectMapper.readTree(contact));
    }

    //all actions for an unauthorized user (no token)
    @Test
    public void test030_addContact_Unauthorized() throws IOException {
        postRequest = new HttpPost(CONTACT_URL);
        postRequest.addHeader("Content-Type", "application/json");

        Contact contact = new Contact("Anna", "Yurchenko", "Tel-Ran");
        String contactAsString = objectMapper.writeValueAsString(contact);

        postRequest.setEntity(new StringEntity(contactAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test031_changeContact_Unauthorized() throws IOException {
        int contactId = getId("");

        putRequest = new HttpPut(CONTACT_URL);
        putRequest.addHeader("Content-Type", "application/json");

        Contact contact = new Contact(contactId, "Anna", "Yurchenko", "Friend");
        String contactAsString = objectMapper.writeValueAsString(contact);

        putRequest.setEntity(new StringEntity(contactAsString));
        response = client.execute(putRequest);

        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    //phones
    @Test
    public void test0032_addPhone_Unauthorized() throws IOException {
        int contactId = getId("");
        postRequest = new HttpPost(PHONE_URL);
        postRequest.addHeader("Content-Type", "application/json");

        Phone phone = new Phone(49, 5768696, contactId);
        String phoneAsString = objectMapper.writeValueAsString(phone);

        postRequest.setEntity(new StringEntity(phoneAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test033_getExistingPhone_Unauthorized() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        getRequest = new HttpGet(PHONE_URL + "/" + phoneId);
        getRequest.addHeader("Content-Type", "application/json");

        response = client.execute(getRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test034_changePhone_Unauthorized() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        putRequest = new HttpPut(PHONE_URL);
        putRequest.addHeader("Content-Type", "application/json");

        Phone phoneJson = new Phone(49, 2222222, phoneId, contactId);
        String phoneAsString = objectMapper.writeValueAsString(phoneJson);
        putRequest.setEntity(new StringEntity(phoneAsString, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    //emails
    @Test
    public void test035_addEmail_Unauthorized() throws IOException {
        int contactId = getId("");
        postRequest = new HttpPost(EMAIL_URL);
        postRequest.addHeader("Content-Type", "application/json");

        Email email = new Email("test@email.com", contactId);
        String emailAsString = objectMapper.writeValueAsString(email);

        postRequest.setEntity(new StringEntity(emailAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test036_getExistingEmail_Unauthorized() throws IOException {
        int contactId = getId("");
        int emailId = getId("/" + contactId + "/emails");

        getRequest = new HttpGet(EMAIL_URL + "/" + emailId);
        getRequest.addHeader("Content-Type", "application/json");

        response = client.execute(getRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test037_changeEmail_Unauthorized() throws IOException {
        int contactId = getId("");
        int emailId = getId("/" + contactId + "/emails");

        putRequest = new HttpPut(EMAIL_URL);
        putRequest.addHeader("Content-Type", "application/json");

        Email emailJson = new Email("test123@email.de", emailId, contactId);
        String emailAsString = objectMapper.writeValueAsString(emailJson);
        putRequest.setEntity(new StringEntity(emailAsString, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    //addresses
    @Test
    public void test038_addAddress_Unauthorized() throws IOException {
        int contactId = getId("");
        postRequest = new HttpPost(ADDRESS_URL);
        postRequest.addHeader("Content-Type", "application/json");

        Address address = new Address("Street", "zip", "City", "Country", contactId);
        String addressAsString = objectMapper.writeValueAsString(address);

        postRequest.setEntity(new StringEntity(addressAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test039_getExistingAddress_Unauthorized() throws IOException {
        int contactId = getId("");
        int addressId = getId("/" + contactId + "/addresses");

        getRequest = new HttpGet(ADDRESS_URL + "/" + addressId);
        getRequest.addHeader("Content-Type", "application/json");

        response = client.execute(getRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test040_changeAddress_Unauthorized() throws IOException {
        int contactId = getId("");
        int addressId = getId("/" + contactId + "/addresses");

        putRequest = new HttpPut(ADDRESS_URL);
        putRequest.addHeader("Content-Type", "application/json");

        Address addressJson = new Address("Street", "zip", "City", "Some other country", addressId, contactId);
        String addressAsString = objectMapper.writeValueAsString(addressJson);
        putRequest.setEntity(new StringEntity(addressAsString, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    //unauthorized deletes
    @Test
    public void test041_deletePhone_Unauthorized() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        deleteRequest = new HttpDelete(PHONE_URL + "/" + phoneId);
        deleteRequest.addHeader("Content-Type", "application/json");

        response = client.execute(deleteRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test042_deleteEmail_Unauthorized() throws IOException {
        int contactId = getId("");
        int emailId = getId("/" + contactId + "/emails");

        deleteRequest = new HttpDelete(EMAIL_URL + "/" + emailId);
        deleteRequest.addHeader("Content-Type", "application/json");

        response = client.execute(deleteRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test043_deleteAddress_Unauthorized() throws IOException {
        int contactId = getId("");
        int addressId = getId("/" + contactId + "/addresses");

        deleteRequest = new HttpDelete(ADDRESS_URL + "/" + addressId);
        deleteRequest.addHeader("Content-Type", "application/json");

        response = client.execute(deleteRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test044_deleteContact_Unauthorized() throws IOException {
        int contactId = getId("");

        deleteRequest = new HttpDelete(CONTACT_URL + "/" + contactId);
        deleteRequest.addHeader("Content-Type", "application/json");

        response = client.execute(deleteRequest);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    //get details from another user
    @Test
    public void test045_getExistingContact_WrongUser() throws IOException {
        int contactId = getId("");
        System.out.println(contactId);

        getRequest = new HttpGet(CONTACT_URL + "/" + contactId);
        header.makeHeader(EMAIL_USER2, PASS_USER2, getRequest);

        response = client.execute(getRequest);

        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        String expectedMessage ="{\"message\":\"Error! You have no permission\"}";

        assertEquals(500, response.getStatusLine().getStatusCode());
        assertEquals(expectedMessage, message);
    }

    @Test
    public void test003_changeContact_WrongUser() throws IOException {
        int contactId = getId("");

        putRequest = new HttpPut(CONTACT_URL);
        header.makeHeader(EMAIL_USER2, PASS_USER2, putRequest);

        Contact contact = new Contact(contactId, "Anna", "Yurchenko", "Friend");
        String contactAsString = objectMapper.writeValueAsString(contact);

        putRequest.setEntity(new StringEntity(contactAsString));
        response = client.execute(putRequest);

        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        String expectedMessage ="{\"message\":\"Error! You have no permission\"}";

        assertEquals(500, response.getStatusLine().getStatusCode());
        assertEquals(expectedMessage, message);
    }

    @Test
    public void test006_getExistingPhone_WrongUser() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        getRequest = new HttpGet(PHONE_URL + "/" + phoneId);
        header.makeHeader(EMAIL_USER2, PASS_USER2, getRequest);

        response = client.execute(getRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        String expectedMessage ="{\"message\":\"Error! You have no permission\"}";

        assertEquals(500, response.getStatusLine().getStatusCode());
        assertEquals(expectedMessage, message);
    }

    @Test
    public void test007_changePhone_WrongUser() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        putRequest = new HttpPut(PHONE_URL);
        header.makeHeader(EMAIL_USER2, PASS_USER2, putRequest);

        Phone phoneJson = new Phone(49, 2222222, phoneId, contactId);
        String phoneAsString = objectMapper.writeValueAsString(phoneJson);
        putRequest.setEntity(new StringEntity(phoneAsString, ContentType.APPLICATION_JSON));

        response = client.execute(putRequest);

        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        String expectedMessage ="{\"message\":\"Error! You have no permission\"}";

        assertEquals(500, response.getStatusLine().getStatusCode());
        assertEquals(expectedMessage, message);
    }

    //back to the authorized user. delete actions
    @Test
    public void test050_deletePhone() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        deleteRequest = new HttpDelete(PHONE_URL + "/" + phoneId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test051_getAllPhones_emptyDb() throws IOException {
        int contactId = getId("");
        getRequest = new HttpGet(CONTACT_URL + "/" + contactId + "/phones");
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("[]", getAll);
    }

    @Test
    public void test052_deleteEmail() throws IOException {
        int contactId = getId("");
        int emailId = getId("/" + contactId + "/emails");

        deleteRequest = new HttpDelete(EMAIL_URL + "/" + emailId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test053_getAllEmails_emptyDb() throws IOException {
        int contactId = getId("");
        getRequest = new HttpGet(CONTACT_URL + "/" + contactId + "/emails");
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("[]", getAll);
    }

    @Test
    public void test054_deleteAddress() throws IOException {
        int contactId = getId("");
        int addressId = getId("/" + contactId + "/addresses");

        deleteRequest = new HttpDelete(ADDRESS_URL + "/" + addressId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test055_getAllAddresses_emptyDb() throws IOException {
        int contactId = getId("");
        getRequest = new HttpGet(CONTACT_URL + "/" + contactId + "/addresses");
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("[]", getAll);
    }

    @Test
    public void test056_deleteContact() throws IOException {
        int contactId = getId("");

        deleteRequest = new HttpDelete(CONTACT_URL + "/" + contactId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        //confirm delete
        getRequest = new HttpGet(CONTACT_URL + "/" + contactId);
        header.makeHeader(EMAIL_USER1, PASS_USER1, getRequest);

        response = client.execute(getRequest);

        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        String expectedMessage = "{\"message\":\"Error! This contact doesn't exist\"}";

        assertEquals(expectedMessage, message);
        assertEquals(500, response.getStatusLine().getStatusCode());
    }

}
