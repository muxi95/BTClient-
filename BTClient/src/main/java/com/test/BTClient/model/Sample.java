package com.test.BTClient.model;

public class Sample {
    private int id;
    private String userid;
    private String batchname;  //批次名
    private String containername;//容器名
    private String samplename;//样品名
    private String weight;//重量
    private String subdate;
    private String ischecked;
    private String hide;
    private String isweight;


    public Sample() {
    }

    public Sample(int id, String userid, String containername, String samplename) {
        this.id = id;
        this.userid = userid;
        this.containername = containername;
        this.samplename = samplename;
    }

    public Sample(String userid,int id , String weight, String isweight) {
        this.id = id;
        this.userid = userid;
        this.weight = weight;
        this.isweight = isweight;
    }

    public  Sample(int id, String userid, String batchname, String containername, String samplename,
                   String weight, String subdate, String ischecked, String hide, String isweight){
        this.id = id;
        this.userid = userid;
        this.batchname = batchname;
        this.containername = containername;
        this.samplename = samplename;
        this.weight = weight;
        this.subdate = subdate;
        this.ischecked = ischecked;
        this.hide = hide;
        this.isweight = isweight;

    }

    public Sample(int id, String userid, String hide, String containername, String samplename, String weight,String isweight) {
        this.id = id;
        this.userid = userid;
        this.hide = hide;
        this.containername = containername;
        this.samplename = samplename;
        this.weight = weight;
        this.isweight = isweight;
    }

    public Sample(int id, String userid, String containername, String samplename, String weight,String isweight) {
        this.id = id;
        this.userid = userid;
        this.containername = containername;
        this.samplename = samplename;
        this.weight = weight;
        this.isweight = isweight;
    }

    public Sample(int id, String userid, String hide){
        this.id = id;
        this.userid = userid;
        this.hide = hide;
    }

    public Sample(int id, String userid, String containername, String samplename, String weight) {
        this.id = id;
        this.userid = userid;
        this.containername = containername;
        this.samplename = samplename;
        this.weight = weight;
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

    public String getContainernamet(){return containername;}
    public  void setContainername(String containername){this.containername = containername;}

    public String getSamplename(){return samplename;}
    public  void setSamplename(String samplename){this.samplename = samplename;}

    public String getSubdate(){return subdate;}
    public  void setSubdate(String subdateb){this.subdate = subdateb;}

    public String getIschecked(){return ischecked;}
    public  void setIschecked(String ischecked){this.isweight = ischecked;}

    public String getHide(){return hide;}
    public  void setHide(String hide){this.hide = hide;}

    public String getIsweight(){return isweight;}
    public  void setIsweight(String isweight){this.isweight = isweight;}

    public String getWeight(){return weight;}
    public  void setWeight(String weight){this.weight = weight;}

}
