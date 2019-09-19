package com.example.adopy.Models;

public class UserModel {

    private String PhotoUrl;
    private String UserName;
    private int UserAge;
    private String Email;
    private String UserCity;
    private String UserGender;
    private String Id;
    private String messaging_token;

    public UserModel(String photoUrl, String userName, int userAge, String email, String userCity, String userGender, String id, String messaging_token) {
        PhotoUrl = photoUrl;
        UserName = userName;
        UserAge = userAge;
        Email = email;
        UserCity = userCity;
        UserGender = userGender;
        Id = id;
        this.messaging_token = messaging_token;
    }

    public UserModel() {
    }

    public String getMessaging_token() {
        return messaging_token;
    }

    public void setMessaging_token(String messaging_token) {
        this.messaging_token = messaging_token;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getUserAge() {
        return UserAge;
    }

    public void setUserAge(int userAge) {
        UserAge = userAge;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserCity() {
        return UserCity;
    }

    public void setUserCity(String userCity) {
        UserCity = userCity;
    }

    public String getUserGender() {
        return UserGender;
    }

    public void setUserGender(String userGender) {
        UserGender = userGender;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
