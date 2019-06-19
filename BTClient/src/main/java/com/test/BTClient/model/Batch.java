package com.test.BTClient.model;

public class Batch {
    private int id;
    private String userid;
    private String batchname;   //批次描述
    private String batchdes;
    private String state;
    private String rizhi;

    public Batch() {
    }

    public  Batch(int id, String userid, String batchname, String batchdes, String state, String rizhi){
        this.id = id;
        this.userid = userid;
        this.batchname = batchname;
        this.batchdes = batchdes;
        this.state = state;
        this.rizhi = rizhi;
    }

    public  Batch(int id, String userid, String batchname, String batchdes){
        this.id = id;
        this.userid = userid;
        this.batchname = batchname;
        this.batchdes = batchdes;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUserid(){return userid;}
    public  void setUserid(String userid){this.userid = userid;}

    public String getBatchname(){return batchname;}
    public  void setBatchname(String batchname){this.batchname = batchname;}

    public String getBatchdes(){return batchdes;}
    public  void setBatchdes(String batchdes){this.batchdes = batchdes;}

    public String getState(){return state;}
    public  void setState(String state){this.state = state;}

    public String getRizhi(){return rizhi;}
    public  void setRizhi(String rizhi){this.rizhi = rizhi;}
}
