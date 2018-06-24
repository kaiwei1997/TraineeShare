package model;

public class Student {
    public String firstName;
    public String lastName;
    public String contact;
    public String address;

    public Student(){ }

    public Student(String firstName, String lastName, String contact, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.address = address;
    }
}
