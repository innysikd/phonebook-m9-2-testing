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
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContactTest extends Header {

    CloseableHttpClient client;
    private String contactUrl = "http://dev.phonebook-2.telran-edu.de/api/contact";
    private String phoneUrl = "http://dev.phonebook-2.telran-edu.de/api/phone";
    private String emailUrl = "http://dev.phonebook-2.telran-edu.de/api/email";
    private String addressUrl = "http://dev.phonebook-2.telran-edu.de/api/address";
    private String email = "test@mail.com";
    private String password = "12345678";
    private HttpPost postRequest;
    private HttpResponse response;
    private HttpGet getRequest;
    private HttpDelete deleteRequest;
    private HttpPut putRequest;
    private ObjectMapper objectMapper = new ObjectMapper();


    public ContactTest() {
    }

    @Before
    public void init() {
        client = HttpClientBuilder.create().build();
    }

    private int getId(String endPoint) throws IOException {
        client = HttpClientBuilder.create().build();
        getRequest = new HttpGet(contactUrl + endPoint);
        makeHeader(email, password, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8").replace("[", "");

        return new JSONObject(getAll).getInt("id");
    }

    //adding and editing contact
    @Test
    public void test001_addContact() throws IOException {
        postRequest = new HttpPost(contactUrl);
        makeHeader(email, password, postRequest);

        Contact contact = new Contact("Anna", "Yurchenko", "Tel-Ran");
        String contactAsString = objectMapper.writeValueAsString(contact);

        postRequest.setEntity(new StringEntity(contactAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test002_getExistingContact() throws IOException {
        int contactId = getId("");

        getRequest = new HttpGet(contactUrl + "/" + contactId);
        makeHeader(email, password, getRequest);

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

        putRequest = new HttpPut(contactUrl);
        makeHeader(email, password, putRequest);

        Contact contact = new Contact(contactId, "Anna", "Yurchenko", "Friend");
        String contactAsString = objectMapper.writeValueAsString(contact);

        putRequest.setEntity(new StringEntity(contactAsString));
        response = client.execute(putRequest);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test004_getChangedContact() throws IOException {
        int contactId = getId("");

        getRequest = new HttpGet(contactUrl + "/" + contactId);
        makeHeader(email, password, getRequest);

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
        postRequest = new HttpPost(phoneUrl);
        makeHeader(email, password, postRequest);

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

        getRequest = new HttpGet(phoneUrl + "/" + phoneId);
        makeHeader(email, password, getRequest);

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

        putRequest = new HttpPut(phoneUrl);
        makeHeader(email, password, putRequest);

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

        getRequest = new HttpGet(phoneUrl + "/" + phoneId);
        makeHeader(email, password, getRequest);

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

        getRequest = new HttpGet(contactUrl + "/" + contactId);
        makeHeader(email, password, getRequest);

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

    @Test
    public void test010_addSecondPhone(){

    }

    @Test
    public void test011_getSecondPhone(){

    }

    @Test
    public void test012_getAllPhones(){

    }

    @Test
    public void test013_getContactWithAllPhones(){

    }

    //adding and editing emails
    @Test
    public void test014_addEmail() throws IOException {
        int contactId = getId("");
        postRequest = new HttpPost(emailUrl);
        makeHeader(email, password, postRequest);

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

        getRequest = new HttpGet(emailUrl + "/" + emailId);
        makeHeader(email, password, getRequest);

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

        putRequest = new HttpPut(emailUrl);
        makeHeader(email, password, putRequest);

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

        getRequest = new HttpGet(emailUrl + "/" + emailId);
        makeHeader(email, password, getRequest);

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

        getRequest = new HttpGet(contactUrl + "/" + contactId);
        makeHeader(email, password, getRequest);

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

    @Test
    public void test019_addSecondEmail(){

    }

    @Test
    public void test020_getSecondEmail(){

    }

    @Test
    public void test021_getAllEmails(){

    }

    @Test
    public void test022_getContactWithAllPhonesAndEmails(){

    }

    //adding and editing addresses
    @Test
    public void test023_addAddress() throws IOException {
        int contactId = getId("");
        postRequest = new HttpPost(addressUrl);
        makeHeader(email, password, postRequest);

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

        getRequest = new HttpGet(addressUrl + "/" + addressId);
        makeHeader(email, password, getRequest);

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

        putRequest = new HttpPut(addressUrl);
        makeHeader(email, password, putRequest);

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

        getRequest = new HttpGet(addressUrl + "/" + addressId);
        makeHeader(email, password, getRequest);

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

        getRequest = new HttpGet(contactUrl + "/" + contactId);
        makeHeader(email, password, getRequest);

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

    @Test
    public void test028_addSecondAddress(){

    }

    @Test
    public void test029_getSecondAddress(){

    }

    @Test
    public void test030_getAllAddresses(){

    }

    @Test
    public void test031_getContactWithAllPhonesEmailsAddresses(){

    }

    //adding a second contact
    @Test
    public void test032_addSecondContact(){

    }

    @Test
    public void test033_getSecondContact(){

    }

    @Test
    public void test034_getAllContacts(){

    }

    //delete actions
    @Test
    public void test035_deletePhone() throws IOException {
        int contactId = getId("");
        int phoneId = getId("/" + contactId + "/phones");

        deleteRequest = new HttpDelete(phoneUrl + "/" + phoneId);
        makeHeader(email, password, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test036_getAllPhones_OneRemains(){

    }

//    @Test
//    public void test037_deleteSecondPhone() throws IOException {
//        int contactId = getId("");
//        int phoneId = getId("/" + contactId + "/phones");
//
//        deleteRequest = new HttpDelete(phoneUrl + "/" + phoneId);
//        makeHeader(email, password, deleteRequest);
//
//        response = client.execute(deleteRequest);
//        assertEquals(200, response.getStatusLine().getStatusCode());
//    }

    @Test
    public void test038_getAllPhones_emptyDb() throws IOException {
        int contactId = getId("");
        getRequest = new HttpGet(contactUrl + "/" + contactId + "/phones");
        makeHeader(email, password, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("[]", getAll);
    }

    @Test
    public void test039_deleteEmail() throws IOException {
        int contactId = getId("");
        int emailId = getId("/" + contactId + "/emails");

        deleteRequest = new HttpDelete(emailUrl + "/" + emailId);
        makeHeader(email, password, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test040_getAllEmails_OneRemains(){

    }

//    @Test
//    public void test041_deleteSecondEmail() throws IOException {
//        int contactId = getId("");
//        int emailId = getId("/" + contactId + "/emails");
//
//        deleteRequest = new HttpDelete(emailId + "/" + emailId);
//        makeHeader(email, password, deleteRequest);
//
//        response = client.execute(deleteRequest);
//        assertEquals(200, response.getStatusLine().getStatusCode());
//    }

    @Test
    public void test042_getAllEmails_emptyDb() throws IOException {
        int contactId = getId("");
        getRequest = new HttpGet(contactUrl + "/" + contactId + "/emails");
        makeHeader(email, password, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("[]", getAll);
    }

    @Test
    public void test043_deleteAddress() throws IOException {
        int contactId = getId("");
        int addressId = getId("/" + contactId + "/addresses");

        deleteRequest = new HttpDelete(addressUrl + "/" + addressId);
        makeHeader(email, password, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test044_getAllAddresses_OneRemains(){

    }

//    @Test
//    public void test045_deleteSecondAddress() throws IOException {
//        int contactId = getId("");
//        int addressId = getId("/" + contactId + "/addresses");
//
//        deleteRequest = new HttpDelete(addressUrl + "/" + addressId);
//        makeHeader(email, password, deleteRequest);
//
//        response = client.execute(deleteRequest);
//        assertEquals(200, response.getStatusLine().getStatusCode());
//    }

    @Test
    public void test046_getAllAddresses_emptyDb() throws IOException {
        int contactId = getId("");
        getRequest = new HttpGet(contactUrl + "/" + contactId + "/addresses");
        makeHeader(email, password, getRequest);
        response = client.execute(getRequest);

        HttpEntity entityAll = response.getEntity();
        String getAll = EntityUtils.toString(entityAll, "UTF-8");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("[]", getAll);
    }

    @Test
    public void test047_deleteContact() throws IOException {
        int contactId = getId("");

        deleteRequest = new HttpDelete(contactUrl + "/" + contactId);
        makeHeader(email, password, deleteRequest);

        response = client.execute(deleteRequest);
        assertEquals(200, response.getStatusLine().getStatusCode());

        //confirm delete
        getRequest = new HttpGet(contactUrl + "/" + contactId);
        makeHeader(email, password, getRequest);

        response = client.execute(getRequest);

        HttpEntity entity = response.getEntity();
        String message = EntityUtils.toString(entity, "UTF-8");
        String expectedMessage = "{\"message\":\"Error! This contact doesn't exist\"}";

        assertEquals(expectedMessage, message);
        assertEquals(500, response.getStatusLine().getStatusCode());
    }

    @Test
    public void test048_deleteSecondContact(){

    }

    @Test
    public void test049_getAllContacts_emptyDb(){

    }

    //all actions without adding the token
    @Test
    public void test050_addContact_Unauthorized() throws IOException {
        postRequest = new HttpPost(contactUrl);
        postRequest.addHeader("Content-Type", "application/json");

        Contact contact = new Contact("Anna", "Yurchenko", "Tel-Ran");
        String contactAsString = objectMapper.writeValueAsString(contact);

        postRequest.setEntity(new StringEntity(contactAsString, ContentType.APPLICATION_JSON));
        response = client.execute(postRequest);

        assertEquals(401, response.getStatusLine().getStatusCode());
    }



}
