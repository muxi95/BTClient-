package com.test.BTClient.model;

public class Rizhi {
    private int id;
    private String batch;
    private String time;
    private String text;

    public Rizhi() {
    }

    public  Rizhi(int id, String batch, String time, String text){
        this.id = id;
        this.batch = batch;
        this.time = time;
        this.text = text;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getBatch(){return batch;}
    public  void setBatch(String batch){this.batch = batch;}

    public String getTime(){return time;}
    public  void setTime(String time){this.time = time;}

    public String getText(){return text;}
    public  void setText(String text){this.text = text;}
}
