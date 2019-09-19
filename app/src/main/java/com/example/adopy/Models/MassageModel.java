package com.example.adopy.Models;

public class MassageModel {
    private String MassageText;
    private String UserName;
    private String MassageId;
    private String WriterId;
    private String ReceiverId;

    public MassageModel(String massageText, String userName, String massageId, String writerId, String receiverId) {
        MassageText = massageText;
        UserName = userName;
        MassageId = massageId;
        WriterId = writerId;
        ReceiverId = receiverId;
    }

    public String getWriterId() {
        return WriterId;
    }

    public void setWriterId(String writerId) {
        WriterId = writerId;
    }

    public String getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(String receiverId) {
        ReceiverId = receiverId;
    }

    public String getMassageText() {
        return MassageText;
    }

    public void setMassageText(String massageText) {
        MassageText = massageText;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMassageId() {
        return MassageId;
    }

    public void setMassageId(String massageId) {
        MassageId = massageId;
    }
}
