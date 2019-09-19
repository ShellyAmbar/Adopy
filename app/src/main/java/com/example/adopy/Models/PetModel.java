package com.example.adopy.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PetModel implements Serializable  {

    private String Name;
    private  String Photo;
    private String Info;
    private String Date;
    private String Location;
    private Boolean Immunized;
    private String Age;
    private String Price;
    private String PostOwnerId;
    private  String PostId;

    public PetModel(String name, String photo, String info, String date, String location, Boolean immunized, String age, String price, String postOwnerId, String postId) {
        Name = name;
        Photo = photo;
        Info = info;
        Date = date;
        Location = location;
        Immunized = immunized;
        Age = age;
        Price = price;
        PostOwnerId = postOwnerId;
        PostId = postId;
    }

    public PetModel() {
    }


    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getPostOwnerId() {
        return PostOwnerId;
    }

    public void setPostOwnerId(String postOwnerId) {
        PostOwnerId = postOwnerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Boolean getImmunized() {
        return Immunized;
    }

    public void setImmunized(Boolean immunized) {
        Immunized = immunized;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }


}
