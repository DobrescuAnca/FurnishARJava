package com.example.furnishar.models;

public class User {

    private String firstName;
    private String email;

    public User() {

    }

    public User(String name, String email) {
        this.firstName = name;
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public String getFirstName(){
        return firstName;
    }
}
