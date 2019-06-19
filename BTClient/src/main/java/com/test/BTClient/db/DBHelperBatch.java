package com.test.BTClient.db;

import android.content.Context;

import com.test.BTClient.model.Batch;

import java.util.ArrayList;

import dev.sqlite.LBSQLiteDatabase;
import dev.sqlite.entity.LBHashMap;

public class DBHelperBatch{
    //添加
    public static Boolean addBatch(Context context, Batch batch) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + batch.getId() + "'";
        Batch uiOld = database.queryEntity(Batch.class, false, where, null, null, null, null);
        if (uiOld == null) {
            return database.insert(batch);
        } else {
            return false;
        }
    }

    //删除
    public static Boolean delBatch(Context context, Batch batch) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + batch.getId() + "'";
        Batch uiOld = database.queryEntity(Batch.class, false, where, null, null, null, null);
        if (uiOld != null) {
            return database.delete(batch);
        } else {
            return false;
        }
    }

    //获得id
    public static Batch getBatch(Context context, String id) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + id + "'";
        Batch batch = database.queryEntity(Batch.class, false, where, null, null, null, null);
        return batch;
    }

    //获得最大id
    public static ArrayList<LBHashMap<String>> getMaxBatchid(Context context) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("SELECT MAX(id) FROM Batch", null);
        return ret;
    }

    //获得所有的批次信息
    public static ArrayList<LBHashMap<String>> getAllBatch(Context context, String userid) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("SELECT * FROM Batch where userid==?", new String[]{userid});
        return ret;
    }
}
