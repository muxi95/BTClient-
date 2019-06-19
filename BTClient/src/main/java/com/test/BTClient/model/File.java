package com.test.BTClient.model;

public class File {
    private int id;
    private String fileindex;
    private String filedate;
    private String filepath;
    private String filetruename;

    public File(){
    }
    public File(int id, String fileindex, String filedate, String filepath, String filetruename){
        this.id = id;
        this.fileindex = fileindex;
        this.filedate = filedate;
        this.filepath = filepath;
        this.filetruename = filetruename;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFileindex(){return fileindex;}
    public  void setFileindex(String fileindex){this.fileindex = fileindex;}

    public String getFiledate(){return filedate;}
    public  void setFiledate(String filedate){this.filedate = filedate;}

    public String getFilepath(){return filepath;}
    public  void setFilepath(String filepath){this.filepath = filepath;}

    public String getFiletruename(){return filetruename;}
    public  void setFiletruename(String filetruename){this.filetruename = filetruename;}
}
