package com.test.BTClient.db;

import android.content.Context;

import com.test.BTClient.model.Sample;

import java.util.ArrayList;

import dev.sqlite.LBSQLiteDatabase;
import dev.sqlite.entity.LBHashMap;

public class DBHelperSample {
    //根据id添加
    public static Boolean addSample(Context context, Sample sample) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + sample.getId() + "'";
        Sample uiOld = database.queryEntity(Sample.class, false, where, null, null, null, null);
        if (uiOld == null) {
            return database.insert(sample);
        } else {
            return false;
        }
    }
        //根据userid添加
    public static Boolean addSample1(Context context, Sample sample) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "userid = '" + sample.getUserid() + "'";
        Sample uiOld = database.queryEntity(Sample.class, false, where, null, null, null, null);
        if (uiOld == null) {
            return database.insert(sample);
        } else {
            return false;
        }
    }

    //删除
    public static Boolean delSample(Context context, Sample sample) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + sample.getId() + "'";
        Sample uiOld = database.queryEntity(Sample.class, false, where, null, null, null, null);
        if (uiOld != null) {
            return database.delete(sample);
        } else {
            return false;
        }
    }

    //根据id获得样品
    public static Sample getSample(Context context, String id) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + id + "'";
        Sample sample = database.queryEntity(Sample.class, false, where, null, null, null, null);
        return sample;
    }

    //获得最大id
    public static ArrayList<LBHashMap<String>> getMaxSampleid(Context context) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("SELECT MAX(id) FROM Sample", null);
        return ret;
    }
    //获得最大id
    public static ArrayList<LBHashMap<String>> getMinSampleid(Context context) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("Sample",new String[]{"min(id)"},"isweight like ?",new String []{"1"},null,null,"id desc",null);
        return ret;
    }

    //获得样品信息
    public static ArrayList<LBHashMap<String>> getSampleX(Context context, String userid) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("SELECT id,containername,samplename,weight,isweight FROM Sample where userid==?", new String[]{userid});
        return ret;
    }

    //更新isweight0
    public static Boolean updateSampleIsWeight0(Context context, Sample sample) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        //String where = "id = '" + sample.getId() + "'";
       // Sample uiOld = database.queryEntity(Sample.class, false, where, null, null, null, null);

            sample.setIsweight("0");
            return database.update(sample);

    }

    //更新isweight1
    public static Boolean updateSampleIsWeight1(Context context, Sample sample) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        //String where = "id = '" + sample.getId() + "'";
       // Sample uiOld = database.queryEntity(Sample.class, false, where, null, null, null, null);
            sample.setIsweight("1");
            return database.update(sample);

    }

    //获得最大id
    /*public  static Sample getMaxid(Context context) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);

        String table =  "Sample" ;
        String[] columns = new  String[] { "id" };
        String selection = "id>=?" ;
        String[] selectionArgs = new  String[]{ "0" };
        String groupBy = "userid" ;
        String having = "id==max(id)" ;
        String orderBy = "id desc" ;
        String limit = "1";
         //database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, null);

        //String where = "id =' " +database.query("Sample","id",null,null,"userid","id>0","id desc","1")  +"'";
        String where = "id='"+database.query(table, columns, null, null, groupBy, having, orderBy, null) +"'";
        Sample sample = database.queryEntity(Sample.class, false, where, null, null, null, null);
        //int id=sample.getId();
           // id++;
        return sample;
        //database.query("Sample","id",null,null,null,null,"id desc",null);
    }*/




}
