package com.test.BTClient.db;

import android.content.Context;

import com.test.BTClient.model.Sadd;

import java.util.ArrayList;

import dev.sqlite.LBSQLiteDatabase;
import dev.sqlite.entity.LBHashMap;

public class DBHelperSadd {
    //根据id添加
    public static Boolean addSadd(Context context, Sadd sadd) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + sadd.getId() + "'";
        Sadd uiOld = database.queryEntity(Sadd.class, false, where, null, null, null, null);
        if (uiOld == null) {
            return database.insert(sadd);
        } else {
            return false;
        }
    }

    //删除
    public static Boolean delSadd(Context context, Sadd sample) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + sample.getId() + "'";
        Sadd uiOld = database.queryEntity(Sadd.class, false, where, null, null, null, null);
        if (uiOld != null) {
            return database.delete(sample);
        } else {
            return false;
        }
    }

    //获得id
    public static Sadd getSadd(Context context, String id) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + id + "'";
        Sadd sadd = database.queryEntity(Sadd.class, false, where, null, null, null, null);
        return sadd;
    }

    //获得最大id
    public static ArrayList<LBHashMap<String>> getMaxSaadid(Context context) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("SELECT MAX(id) FROM Sadd", null);
        return ret;
    }
}
