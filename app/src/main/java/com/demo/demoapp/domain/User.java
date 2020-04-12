package com.demo.demoapp.domain;

public class User {
    private String email;
    private String name;
    private String photoUrl;
    private String token;

    private User() {}

    private static User userInstance;

    public static User userInstance() {
        if (userInstance != null)
            return userInstance;

        userInstance = new User();
        return userInstance;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName(){
        return name;
    }

    public String getToken(){
        return token;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }
}
