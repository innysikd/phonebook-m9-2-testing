package api.jsonObjects;

import java.util.ArrayList;
import java.util.List;

public class Contact {

    int id;
    String firstName;
    String lastName;
    String description;
    List<Phone> phoneNumbers = new ArrayList<>();
    List<Address> addresses = new ArrayList<>();
    List<Email> emails = new ArrayList<>();

    public Contact(int id, String firstName, String lastName, String description) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }

    public Contact(String firstName, String lastName, String description) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }

    public Contact(int id, String firstName, String lastName, String description, List<Phone> phoneNumbers) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.phoneNumbers = phoneNumbers;
    }

    public Contact(int id, String firstName, String lastName, String description, List<Phone> phoneNumbers, List<Email> emails) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.phoneNumbers = phoneNumbers;
        this.emails = emails;
    }

    public Contact(int id, String firstName, String lastName, String description, List<Phone> phoneNumbers, List<Email> emails, List<Address> addresses) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.phoneNumbers = phoneNumbers;
        this.emails = emails;
        this.addresses = addresses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Phone> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<Phone> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }
}
