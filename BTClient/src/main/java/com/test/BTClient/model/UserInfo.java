package com.test.BTClient.model;

public class UserInfo {

    private int id;
    private String userid;
    private String username;
    private String grade;//表示时间
    private String psd;
    private String email;
    private String unlocked;
    private String checked;
    private String mobile;

    public UserInfo() {
    }

    public UserInfo(int id, String userid, String grade, String username, String psd, String email,
                    String unlocked, String checked, String mobile) {
        this.id = id;
        this.username = username;
        this.mobile = mobile;
        this.userid = userid;
        this.checked = checked;
        this.email = email;
        this.grade = grade;
        this.psd = psd;
        this.unlocked = unlocked;
    }

    public UserInfo(int id, String userid, String psd, String grade){
        this.id = id;
        this.userid = userid;
        this.psd = psd;
        this.grade = grade;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUserid(){return userid;}
    public  void setUserid(String userid){this.userid = userid;}

    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade ) {
        this.grade = grade;
    }

    public String getPsd() {
        return psd;
    }
    public void setPsd(String psd ) {
        this.psd  = psd;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUnlocked() {
        return unlocked;
    }
    public void setUnlocked(String unlocked) {
        this.unlocked = unlocked;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getChecked() {
        return checked;
    }
    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
