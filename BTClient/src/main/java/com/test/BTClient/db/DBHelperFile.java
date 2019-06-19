package com.test.BTClient.db;

import android.content.Context;

import com.test.BTClient.model.File;

import java.util.ArrayList;

import dev.sqlite.LBSQLiteDatabase;
import dev.sqlite.entity.LBHashMap;

public class DBHelperFile {
    //添加
    public static Boolean addFile(Context context, File file) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + file.getId() + "'";
        File uiOld = database.queryEntity(File.class, false, where, null, null, null, null);
        if (uiOld == null) {
            return database.insert(file);
        } else {
            return false;
        }
    }

    //删除
    public static Boolean delFile(Context context, File file) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + file.getId() + "'";
        File uiOld = database.queryEntity(File.class, false, where, null, null, null, null);
        if (uiOld != null) {
            return database.delete(file);
        } else {
            return false;
        }
    }

    //获得id
    public static File getFile(Context context, String id) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + id + "'";
        File file = database.queryEntity(File.class, false, where, null, null, null, null);
        return file;
    }

    //获得最大id
    public static ArrayList<LBHashMap<String>> getMaxFileid(Context context) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("SELECT MAX(id) FROM File", null);
        return ret;
    }

}
