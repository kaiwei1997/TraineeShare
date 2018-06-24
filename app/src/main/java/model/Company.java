package model;

public class Company {
    public String company_name;
    public String company_address;
    public String contact_person_Fn;
    public String contact_person_Ln;
    public String company_contact;

    public Company(){

    }

    public Company(String company_name, String company_address, String contact_person_Fn, String contact_person_Ln, String company_contact) {
        this.company_name = company_name;
        this.company_address = company_address;
        this.contact_person_Fn = contact_person_Fn;
        this.contact_person_Ln = contact_person_Ln;
        this.company_contact = company_contact;
    }

    public java.lang.String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(java.lang.String company_name) {
        this.company_name = company_name;
    }

    public java.lang.String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(java.lang.String company_address) {
        this.company_address = company_address;
    }

    public java.lang.String getContact_person_Fn() {
        return contact_person_Fn;
    }

    public void setContact_person_Fn(java.lang.String contact_person_Fn) {
        this.contact_person_Fn = contact_person_Fn;
    }

    public java.lang.String getContact_person_Ln() {
        return contact_person_Ln;
    }

    public void setContact_person_Ln(java.lang.String contact_person_Ln) {
        this.contact_person_Ln = contact_person_Ln;
    }

    public java.lang.String getCompany_contact() {
        return company_contact;
    }

    public void setCompany_contact(java.lang.String company_contact) {
        this.company_contact = company_contact;
    }
}
