package com.test.BTClient.db;


import android.content.Context;

import com.test.BTClient.model.Rizhi;

import java.util.ArrayList;

import dev.sqlite.LBSQLiteDatabase;
import dev.sqlite.entity.LBHashMap;

public class DBHelperRizhi {
    //添加
    public static Boolean addRizhi(Context context, Rizhi rizhi) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + rizhi.getId() + "'";
        Rizhi uiOld = database.queryEntity(Rizhi.class, false, where, null, null, null, null);
        if (uiOld == null) {
            return database.insert(rizhi);
        } else {
            return false;
        }
    }

    //删除
    public static Boolean delRizhi(Context context, Rizhi rizhi) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + rizhi.getId() + "'";
        Rizhi uiOld = database.queryEntity(Rizhi.class, false, where, null, null, null, null);
        if (uiOld != null) {
            return database.delete(rizhi);
        } else {
            return false;
        }
    }

    //获得id
    public static Rizhi getRizhi(Context context, String id) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + id + "'";
        Rizhi rizhi = database.queryEntity(Rizhi.class, false, where, null, null, null, null);
        return rizhi;
    }

    //获得最大id
    public static ArrayList<LBHashMap<String>> getMaxRizhiid(Context context) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("SELECT MAX(id) FROM Rizhi", null);
        return ret;
    }

}
