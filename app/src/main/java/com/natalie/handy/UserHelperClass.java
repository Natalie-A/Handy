package com.natalie.handy;
public class UserHelperClass {
    String full_name, email_address;
    int phone_number;
    String password;

    public UserHelperClass(String full_name, String email_address, int phone_number, String password) {
        this.full_name = full_name;
        this.email_address = email_address;
        this.phone_number = phone_number;
        this.password = password;

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

    public int getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(int phone_number) {
        this.phone_number = phone_number;
    }
}
