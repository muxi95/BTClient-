package com.test.BTClient.model;

public class Sadd {
    private int id;
    private String userid;
    private String num;
    private String rqqs;
    private String rqqz;
    private String ypqs;
    private String ypqz;

    public Sadd() {
    }

    public Sadd(int id, String userid, String num, String rqqs, String rqqz, String ypqs, String ypqz) {
        this.id = id;
        this.userid = userid;
        this.num = num;
        this.rqqs = rqqs;
        this.rqqz = rqqz;
        this.ypqs = ypqs;
        this.ypqz = ypqz;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUserid(){return userid;}
    public  void setUserid(String userid){this.userid = userid;}

    public String getNum(){return num;}
    public  void setNum(String num){this.num = num;}

    public String getRqqs(){return rqqs;}
    public  void setRqqs(String rqqs){this.rqqs = rqqs;}

    public String getRqqz(){return rqqz;}
    public  void setRqqz(String rqqz){this.rqqz = rqqz;}

    public String getYpqs(){return ypqs;}
    public  void setYpqs(String ypqs){this.ypqz = ypqs;}

    public String getYpqz(){return ypqz;}
    public  void setYpqz(String ypqz){this.ypqz = ypqz;}
}
