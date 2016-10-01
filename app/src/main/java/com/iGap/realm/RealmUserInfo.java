package com.iGap.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmUserInfo extends RealmObject {

    private boolean registrationStatus;
    private long userId;
    private String userName;
    private String countryISOCode;
    private String email;
    private String nickName;
    private String gender;
    private String phoneNumber;

    private RealmList<RealmAvatarPath> avatarPath;

    private String token;

    //===Setters


    public RealmList<RealmAvatarPath> getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(RealmList<RealmAvatarPath> avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setUserRegistrationState(boolean value) {
        this.registrationStatus = value;
    }

    public void setUserId(long value) {
        this.userId = value;
    }

    public void setUserName(String value) {
        this.userName = value;
    }

    public void setCountryISOCode(String value) {
        this.countryISOCode = value;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public void setNickName(String value) {
        this.nickName = value;
    }

    public void setGender(String value) {
        this.gender = value;
    }

    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }


    public void setToken(String value) {
        this.token = value;
    }

    //===Getters

    public boolean getUserRegistrationState() {
        return this.registrationStatus;
    }

    public long getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getCountryISOCode() {
        return this.countryISOCode;
    }

    public String getEmail() {
        return this.email;
    }

    public String getNickName() {
        return this.nickName;
    }

    public String getGender() {
        return this.gender;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }


    public String getToken() {
        return this.token;
    }

}
