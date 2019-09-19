package com.example.adopy.Models;

public class GroupModel {

    private String GroupName;
    private String OwnerId;
    private String OwnerName;
    private String GroupId;
    private String GroupCreatedTime;
    private int NumOfParticipants;
    private String PhotoUrl;


    public GroupModel(String groupName, String ownerId, String ownerName, String groupId, String groupCreatedTime, int numOfParticipants, String photoUrl) {
        GroupName = groupName;
        OwnerId = ownerId;
        OwnerName = ownerName;
        GroupId = groupId;
        GroupCreatedTime = groupCreatedTime;
        NumOfParticipants = numOfParticipants;
        PhotoUrl = photoUrl;
    }

    public String getGroupName() {
        return GroupName;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.PhotoUrl = photoUrl;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getOwnerId() {
        return OwnerId;
    }

    public void setOwnerId(String ownerId) {
        OwnerId = ownerId;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupCreatedTime() {
        return GroupCreatedTime;
    }

    public void setGroupCreatedTime(String groupCreatedTime) {
        GroupCreatedTime = groupCreatedTime;
    }

    public int getNumOfParticipants() {
        return NumOfParticipants;
    }

    public void setNumOfParticipants(int numOfParticipants) {
        NumOfParticipants = numOfParticipants;
    }
}
