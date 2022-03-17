package com.daud.ghorsara;

public class ProfileMc {
    private String UserName;
    private String UserPhone;
    private String UserEmail;
    private String UserImage;

    public ProfileMc() {
    }

    public ProfileMc(String userName, String userPhone, String userEmail, String userImage) {
        UserName = userName;
        UserPhone = userPhone;
        UserEmail = userEmail;
        UserImage = userImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }
}
