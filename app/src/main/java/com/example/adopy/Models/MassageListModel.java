package com.example.adopy.Models;

public class MassageListModel {
    private String Url;
    private String Name;
    private String MassageTitle;
    private String MassageListId;
    private String UserId;


    public MassageListModel(String url, String name, String massageTitle, String massageListId, String userId) {
        Url = url;
        Name = name;
        MassageTitle = massageTitle;
        MassageListId = massageListId;
        UserId = userId;
    }

    public String getUrl() {
        return Url;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMassageTitle() {
        return MassageTitle;
    }

    public void setMassageTitle(String massageTitle) {
        MassageTitle = massageTitle;
    }

    public String getMassageListId() {
        return MassageListId;
    }

    public void setMassageListId(String massageListId) {
        MassageListId = massageListId;
    }
}
