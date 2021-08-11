package com.natalie.handy;

public class UserHelperClass {
    String full_name, email_address, location, phone_number;

    public UserHelperClass(String full_name, String email_address, String location, String phone_number) {
        this.full_name = full_name;
        this.email_address = email_address;
        this.location = location;
        this.phone_number = phone_number;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
